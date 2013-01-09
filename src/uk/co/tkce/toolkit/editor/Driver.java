package uk.co.tkce.toolkit.editor;

import javax.swing.*;

public class Driver
{
    private static boolean full = false;

    public static void main(String[] args)
    {
        try
        {
            System.out.println(System.getProperty("os.name"));
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new MainWindow();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
