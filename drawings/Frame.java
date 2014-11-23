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
    AnimatingPanel panel1;
    AnimatingPanel panel2;

    JPanel content;
    JPanel environmentPanel;

    public Frame()
    {
        super("MY PROGRAM");

        environmentPanel = new JPanel(new GridLayout(1, 0, 10, 0));
        environmentPanel.setOpaque(false);

        content = new JPanel(new BorderLayout());
        content.add(environmentPanel, BorderLayout.CENTER);
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setOpaque(false);
    }

    public void addPanel(AnimatingPanel panel)
    {
        if (panel1 == null) {
            panel1 = panel;
            environmentPanel.add(panel1);
        } else if (panel2 == null) {
            panel2 = panel;
            environmentPanel.add(panel2);
        } else {
            System.err.println("We only support 2 panels, this panel was ignored, loser.");
        }
    }

    public void run() {
        setBackground(Color.DARK_GRAY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((screenWidth - 1024)/2 , (screenHeight - 600)/2);
        setSize(new Dimension(1024, 600));
        setContentPane(content);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setResizable(false);

        if (panel1 != null)
            panel1.startAnimation();
        if (panel2 != null)
            panel2.startAnimation();
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
