package uk.co.tkce.engine.InternalObjects;

import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

/*
 * Class to provide canvas services to the Toolkit uk.co.tkce.engine.
 */
public class TransCanvas
{
    private BufferedImage content;
    private Graphics2D g;
    private int width;
    private int height;

    public TransCanvas(int width, int height)
    {
        this.height = height;
        this.width = width;
    }

    public void drawLine(int startX, int startY, int endX, int endY)
    {
        glPushMatrix();
        glBegin(GL_LINE);
        {
            glVertex2f(startX, startY);
            glVertex2f(endX, endY);
        }
        glEnd();
        glPopMatrix();
    }

    public void drawRect(int x, int y, int width, int height)
    {
        glBegin(GL_LINE_LOOP);
        {
            glVertex2f(x, y);
            glVertex2f(x + width, y);
            glVertex2f(x + width, y + height);
            glVertex2f(x , y + height);
        }
        glEnd();
    }

    public void fillRect(int x, int y, int width, int height)
    {
        glBegin(GL_QUADS);
        {
            glVertex2f(x, y);
            glVertex2f(x + width, y);
            glVertex2f(x + width, y + height);
            glVertex2f(x , y + height);
        }
        glEnd();
    }

    public void drawCircle(int x, int y, int radius)
    {
        g.drawOval(x, y, radius, radius);
    }

    public void fillCircle(int x, int y, int radius)
    {
        g.fillOval(x, y, radius, radius);
    }

    public void drawImage(String fileName, int x, int y, double scale)
    {
        try
        {
            FileInputStream fis = new FileInputStream(new File(fileName));
            g.drawImage(ImageIO.read(fis), x, y, null);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void setColor(Color color)
    {
        glColor3f(color.getRed(), color.getGreen(), color.getBlue());
    }

    public BufferedImage getCanvasToRender()
    {
        return content;
    }
}
