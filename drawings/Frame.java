package drawings;

import java.awt.*;
import javax.swing.*;

/**
 * The basic frame that holds an animating panel
 * 
 * @author Nick Walther
 */
public class Frame extends JFrame
{
    AnimatingPanel panel;

    public Frame()
    {
        super("MY PROGRAM");
    }

    public void setPanel(AnimatingPanel panel)
    {
        this.panel = panel;

        JPanel content = new JPanel(new BorderLayout());
        content.add(panel, BorderLayout.CENTER);
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((screenWidth - 800)/2 , (screenHeight - 600)/2);
        setSize(new Dimension(800, 600));
        setContentPane(content);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setResizable(false);

        panel.startAnimation();
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new Frame();
                }
            });
    }
}
