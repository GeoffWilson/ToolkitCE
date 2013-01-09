package uk.co.tkce.engine;

import uk.co.tkce.toolkit.types.Board;
import uk.co.tkce.toolkit.types.Player;
import uk.co.tkce.toolkit.types.Program;
import uk.co.tkce.toolkit.types.Project;

import java.awt.*;

/**
 * This class manages the execution of the game, it is responsible for allocating time to rending
 * processing RPG code and managing the virtual system that powers the TransCE uk.co.tkce.engine.
 * <p/>
 * Execution is as follows in a standard 2D RPG project
 * Load Main File - > Execute start program - > Send to start board
 * some non-standard programs may have no start board and simply loop the start program
 *
 * @author geoff wilson
 * @version svn
 */
public class GameControl implements Runnable
{
    // Constants
    public final int IN_MENU = 0;
    public final int IN_BATTLE = 1;
    public final int IN_GAME = 2;
    public final int PRG_RUNNING = 3;
    public final int PAUSED = 10;

    private int state = 3;
    private boolean paused = false;

    // Core modules
    private GraphicsEngine graphicsEngine;
    private Party party;

    // Currently Executing Main File
    private Project activeGame;

    // Video Settings
    private int resolutionX;
    private int resolutionY;
    private int colorDepth;
    private boolean fullScreen;

    // Initial Game Settings
    private Board startBoard;
    private Player startPlayer;
    private Program startProgram;

    /**
     * Constructor for GameControl, responsible for loading the main file, and collecting important game settings
     * creates an instance of both the graphics and code engines.
     *
     * @param mainFile RPG Toolkit 3 / CE Main File
     */
    public GameControl(Project mainFile)
    {
        activeGame = mainFile;
        this.getGameSettings();

        graphicsEngine = new GraphicsEngine(this); // create the graphics engine
        party = new Party(startPlayer); // not used yet

        // Set the initial values for the graphics engine
        graphicsEngine.switchPlayer(startPlayer);
        graphicsEngine.switchBoard(startBoard);

        // Ensure objects are loaded for physics
        graphicsEngine.preparePhysics();

        graphicsEngine.begin();
    }

    public void pauseGame()
    {
        paused = !paused;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }

    /**
     * Method to load the initial game settings from the main file
     */
    private void getGameSettings()
    {
        switch (activeGame.getResolutionMode())
        {
            case 0: // 640 x 480
                resolutionX = 640;
                resolutionY = 480;
                break;
            case 1: // 800 x 600
                resolutionX = 800;
                resolutionY = 600;
                break;
            case 2: // 1024 x 768
                resolutionX = 1024;
                resolutionY = 768;
                break;
            case 4: // Custom
                resolutionX = (int) activeGame.getResolutionWidth();
                resolutionY = (int) activeGame.getResolutionHeight();
                break;
        }

        fullScreen = activeGame.getFullscreenMode();

        // Forcing 32bpp color depth on Windows, Linux requires BIT_DEPTH_MULTI or the display mode will return invalid
        if (System.getProperty("os.name").equals("Linux")) colorDepth = DisplayMode.BIT_DEPTH_MULTI;
        else colorDepth = 32;

        startBoard = activeGame.getInitBoard();
        startPlayer = activeGame.getInitChar();
        startProgram = activeGame.getStartupPrg();
    }

    public Dimension getGameResolution()
    {
        return new Dimension(resolutionX, resolutionY);
    }

    public boolean isFullScreen()
    {
        return fullScreen;
    }

    public int getColorDepth()
    {
        return colorDepth;
    }

    public Party getParty()
    {
        return party;
    }

    @Override
    public void run()
    {
        // Start the game
        graphicsEngine.begin();
    }
}
