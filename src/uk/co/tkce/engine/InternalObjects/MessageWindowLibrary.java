package uk.co.tkce.engine.InternalObjects;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 */
public class MessageWindowLibrary
{
    private BufferedImage backgroundImage;
    private String text;
    private Font font;
    private int width;
    private int height;

    private Graphics2D g;

    /**
     * Creates a Message Window using the default values.
     */
    public MessageWindowLibrary(String text)
    {
        try
        {
            backgroundImage = ImageIO.read(new File(System.getProperty("project.path") + "/bitmap/mwin.png"));
            font = new Font("Consolas", Font.BOLD, 22);
            this.text = text;
            g = backgroundImage.createGraphics();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void setBackground(String fileName)
    {
        try
        {
            ImageIO.read(new File(fileName));
        }
        catch (IOException e)
        {
            System.out.println("Background image " + fileName + " not found");
        }

    }

    public void setText(String string)
    {
        this.text = string;
    }

    public void setFont(Font font)
    {
        this.font = font;
    }

    public String getText()
    {
        return text;
    }

    public Font getFont()
    {
        return font;
    }

    public BufferedImage getMessageWindowToRender()
    {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
        g.setFont(font);
        g.drawString(text, 20, 35);
        g.dispose();
        return backgroundImage;
    }
}
