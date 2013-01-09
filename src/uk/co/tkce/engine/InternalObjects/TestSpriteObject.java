package uk.co.tkce.engine.InternalObjects;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Geoff
 * Date: 17/07/12
 * Time: 00:22
 * To change this template use File | Settings | File Templates.
 */
public class TestSpriteObject {

    public BufferedImage image;
    private int x;
    private int y;
    private int speed;

    public TestSpriteObject(int x, int y)
    {
        try
        {
            FileInputStream fis = new FileInputStream(System.getProperty("project.path") + "/bitmap/shot.png");
            image = ImageIO.read(fis);
            this.fired(x, y);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void fired(int x, int y)
    {
        this.x = x;
        this.y = y;
        Timer moveTimer = new Timer();
        moveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setX(getX() + 2);
            }
        },0, 5);

    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

}
