package uk.co.tkce.engine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import uk.co.tkce.engine.InternalObjects.TransCanvas;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * This class is used to test things
 */
public class TestDriver
{
    public static void main(String[] args)
    {
        try
        {
            new TestDriver();
        }
        catch (LWJGLException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public TestDriver() throws LWJGLException
    {
        // Setup the window
        Display.setTitle("Toolkit CE (v2.0.0-svn) - pre-alpha code");
        Display.setSwapInterval(60);
        Display.setDisplayMode(new org.lwjgl.opengl.DisplayMode(800, 600));
        Display.setVSyncEnabled(true);

        // Attempt to create game window
        Display.create();

        this.setupGL();
        this.doTests();
    }

    private void setupGL()
    {
        // disable the OpenGL depth test since we're rendering 2D graphics
        glDisable(GL_DEPTH_TEST);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

    }

    private void doTests()
    {

        TransCanvas tc = new TransCanvas(800, 600);
        while (true)
        {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                    glMatrixMode(GL_MODELVIEW);
                    glLoadIdentity();


            tc.setColor(new Color(255, 0, 100));
            tc.drawLine(50, 50, 150, 150);
        }
    }
}
