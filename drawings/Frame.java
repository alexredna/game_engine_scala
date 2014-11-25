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
    public Frame(String name)
    {
        super(name);

    }

    public void run() {
        //setBackground(Color.DARK_GRAY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((screenWidth - 1024)/2 , (screenHeight - 600)/2);
        //setSize(new Dimension(1024, 600));
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        pack();
        setVisible(true);
        setResizable(false);
    }
}
