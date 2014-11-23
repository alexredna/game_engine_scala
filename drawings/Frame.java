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
    public Frame()
    {
        super("MY PROGRAM");

        /*content = new JPanel(new BorderLayout());
        content.add(environmentPanel, BorderLayout.CENTER);
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setOpaque(false);*/
    }

    public void run() {
        //setBackground(Color.DARK_GRAY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((screenWidth - 1024)/2 , (screenHeight - 600)/2);
        setSize(new Dimension(1024, 600));
        //setContentPane(content);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setResizable(false);
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
