package drawings;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

public class AnimatingPanel extends JPanel implements Runnable
{
    // -----------------------------------------------------------------
    // all of these variables deal with the animation
    // ignore all except DEFAULT_FPS, that may be changed
    private static final int DEFAULT_FPS = 30;
    private static final int NO_DELAYS_PER_YIELD = 16;
    private static int MAX_FRAME_SKIPS = 5;
    private Thread animator;
    private volatile boolean isRunning = false;
    private long period = 1000 / DEFAULT_FPS * 1000000L; // in ns
    private Graphics2D dbg2;
    private BufferedImage dbImage = null;
    private long beforeTime;

    // -----------------------------------------------------------------
    // container
    private JFrame frame;
    // children
    /** declare children here */
    private java.util.List<AnimatingChild> children;

    public AnimatingPanel(JFrame frame)
    {
        this.frame = frame;

        children = Collections.synchronizedList(new ArrayList<AnimatingChild>());

        /** add Mouse, MouseMotion, Component, and Key Listeners here (optional) */
    }

    public void addChild(AnimatingChild child) {
        children.add(child);
    }

    public void startAnimation()
    {
        if (animator == null || !animator.isAlive()) {
            animator = new Thread(this);
            animator.setName("Animations Thread");
            animator.start();
        }
    }

    public void run()
    {
        long afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;
        long excess = 0L;

        isRunning = true;
        beforeTime = System.nanoTime();

        while (isRunning) {
            update();
            render();
            paintPanel();

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime/1000000L);
                } catch (InterruptedException ex) { }
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            } else {
                excess -= sleepTime;
                overSleepTime = 0L;
                if (++noDelays >= NO_DELAYS_PER_YIELD) {
                    Thread.yield();
                    noDelays = 0;
                }
            }

            beforeTime = System.nanoTime();

            int skips = 0;
            while (excess > period && skips < MAX_FRAME_SKIPS) {
                excess -= period;
                update();
                skips++;
            }
        }
    }

    private void update()
    {
        /** Call upon all children the animate method */
        synchronized (children) {
            ListIterator<AnimatingChild> it = children.listIterator();
            while (it.hasNext()) {
                it.next().animate();
            }
        }
    }

    private void render()
    {
        if (dbImage == null){
            if (getWidth() > 0 && getHeight() > 0)
                dbImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            if (dbImage == null)
                return;
            else
                dbg2 = dbImage.createGraphics();
        }

        // Clear buffered image first with all white
        dbg2.setColor(Color.WHITE);
        dbg2.fillRect(0, 0, getWidth(), getHeight());

        /** Call upon all children the draw method */
        synchronized (children) {
            ListIterator<AnimatingChild> it = children.listIterator();
            while (it.hasNext()) {
                it.next().draw(dbg2);
            }
        }
    }

    private void paintPanel()
    {
        Graphics g;
        try {
            g = this.getGraphics();
            if (g != null && dbImage != null)
                g.drawImage(dbImage, 0, 0, null);
            Toolkit.getDefaultToolkit().sync();
            g.dispose();
        } catch (Exception e) {
            System.out.println("Graphics context error: " + e);
            System.exit(0);
        }
    }
}
