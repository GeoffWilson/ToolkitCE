package uk.co.tkce.engine;

import uk.co.tkce.toolkit.types.Enemy;
import uk.co.tkce.toolkit.types.Project;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BattleSystem
{
    private GameControl control;
    private BufferStrategy buffer;
    private boolean battleOver = false;
    private BufferedImage backgroundImage;
    private Dimension resolution;
    private Project activeGame;

    // Array of Enemies
    private ArrayList<Enemy> enemies;

    // Players party
    private Party party;

    public BattleSystem(GameControl control)
    {
        this.control = control;
        this.resolution = control.getGameResolution();
        this.party = control.getParty();
    }

    public void configureBattleSystem(BufferStrategy buffer)
    {
        try
        {
            this.buffer = buffer;

            // Load background image
            FileInputStream fis = new FileInputStream(System.getProperty("project.path") + "/bitmap/fight.jpg");
            backgroundImage = ImageIO.read(fis);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void doBattle()
    {
        System.out.println(resolution.width + " , " + resolution.height);
        while (!battleOver)
        {
            // Configure Graphics
            Graphics2D g = (Graphics2D) buffer.getDrawGraphics();

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, resolution.width, resolution.height);

            // Draw fighting background
            g.drawImage(backgroundImage, 0, 0, resolution.width, resolution.height, null);

            // Draw Interface
            g.setColor(Color.BLUE);
            g.fillRect(0, resolution.height - 150, resolution.width, 150);

            buffer.show();
        }
    }

    private void fightLogic()
    {

    }
}
