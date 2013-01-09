package uk.co.tkce.engine;

import net.java.games.input.Component;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import uk.co.tkce.engine.InternalObjects.MessageWindowLibrary;
import uk.co.tkce.engine.InternalObjects.TestSpriteObject;
import uk.co.tkce.engine.VGM.MusicEmu;
import uk.co.tkce.engine.VGM.VGMPlayer;
import uk.co.tkce.toolkit.types.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author geoff wilson (contact at gawilson.net)
 * @version svn
 */
public class GraphicsEngine
{
    private BufferStrategy strategy;
    private GameControl control;
    private TileSetCache cache;
    private PhysicsEngine physicsEngine;
    private XboxControllerSupport xbcs;
    private AudioEngine audio;
    private VGMPlayer vgm;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    private boolean finished = false;

    private int currentDirection;

    private int fps = 0;
    private int lastFPS = 0;
    private long firstFrame;

    private Board activeBoard;
    private Player activePlayer;
    private BufferedImage playerImage;
    private ArrayList<BoardSprite> sprites;
    private ConcurrentLinkedQueue<TestSpriteObject> testSpriteMap;

    private int resolutionX;
    private boolean fullScreen = false;
    private int resolutionY;

    private BufferedImage boardLayers[];
    private boolean boardChanged = false;

    private BufferedImage overlayImage;
    private Graphics2D g;

    int shiftX = 0;
    int shiftY = 0;
    int playerMove = 3;

    boolean vPressed = false;
    boolean dPressed = false;
    boolean fPressed = false;
    boolean drawMenu = false;
    boolean spacePressed = false;
    boolean shotFired = false;

    int pressCount = 0;
    int dPressCount = 0;
    int spacePressCount = 0;

    int layers;
    int width;
    int height;

    TKVector collisionVector;
    TKVector activationVector;

    /**
     * Creates an instance of the graphics engine, uses the resolution details obtained from
     * the main file.
     *
     * @param control Loop back to game control for making requests!
     */
    public GraphicsEngine(GameControl control)
    {
        this.control = control;
        cache = new TileSetCache();

        // create a frame to contain our game
        Dimension d = control.getGameResolution();
        resolutionX = (int) d.getWidth();
        resolutionY = (int) d.getHeight();
        fullScreen = control.isFullScreen();
        int colorDepth = control.getColorDepth();

        Frame container = new Frame("Toolkit CE (v1.0.0-svn) - pre-alpha code");
        container.setIgnoreRepaint(true);

        testSpriteMap = new ConcurrentLinkedQueue<>();

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();

        //this.fullScreen = true;

        if (this.fullScreen)
        {
            container.setUndecorated(true);
            if (graphicsDevice.isFullScreenSupported())
            {
                graphicsDevice.setFullScreenWindow(container);
            }
            else
            {
                System.out.println("Full screen is not supported on your system :(");
            }
        }
        if (graphicsDevice.isDisplayChangeSupported())
        {
            graphicsDevice.setDisplayMode(new DisplayMode(this.resolutionX, this.resolutionY, colorDepth, DisplayMode.REFRESH_RATE_UNKNOWN));
        }

        container.setPreferredSize(new Dimension(resolutionX, resolutionY));
        container.setLayout(new BorderLayout());
        container.setBounds(0, 0, resolutionX, resolutionY);
        container.pack();
        container.setResizable(false);
        container.setVisible(true);
        container.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        container.requestFocus();

        Canvas c = new Canvas();
        c.setBounds(0, 0, resolutionX, resolutionY);
        c.setIgnoreRepaint(true);
        c.addKeyListener(new KeyInputHandler());
        c.requestFocus();

        container.add(c, BorderLayout.CENTER);

        c.createBufferStrategy(2);
        strategy = c.getBufferStrategy();

        overlayImage = new BufferedImage(resolutionX, 100, BufferedImage.TYPE_INT_ARGB);

        g = overlayImage.createGraphics();
        g.setColor(new Color(100, 100, 100, 175));
        g.fillRect(0, 0, resolutionX, 100);
        g.dispose();

        // Create graphics object from the buffer strategy
        g = (Graphics2D) strategy.getDrawGraphics();

        xbcs = new XboxControllerSupport();
    }

    public void preparePhysics()
    {
        // Setup the physics engine
        physicsEngine = new PhysicsEngine();
        physicsEngine.setCollisionDelta(playerMove);
        physicsEngine.setActivePlayer(activePlayer);
        physicsEngine.loadNewBoardVectors(activeBoard);
    }

    /**
     * Switches the active board in the engine, this change will take effect the next time
     * the rendering code is called.
     *
     * @param newBoard Board file to switch to
     */
    public void switchBoard(Board newBoard)
    {
        activeBoard = newBoard;
        activeBoard.loadTilesForEngine(cache);

        activePlayer.setXLocation(activeBoard.getStartingPositionX() - (activePlayer.getAnimationFrame().getWidth() / 2));
        activePlayer.setYLocation(activeBoard.getStartingPositionY() - activePlayer.getAnimationFrame().getHeight());

        layers = activeBoard.getLayers();
        width = activeBoard.getWidth();
        height = activeBoard.getHeight();

        int maxShiftX = 0;
        int maxShiftY = 0;
        shiftX = 0;
        shiftY = 0;

        boolean noXScroll = width * 32 <= resolutionX;
        boolean noYScroll = height * 32 <= resolutionY;

        if ((width * 32) >= resolutionX)
        {
            maxShiftX = (width * 32) - resolutionX;
        }

        if ((height * 32) >= resolutionY)
        {
            maxShiftY = (height * 32) - resolutionY;
        }

        int halfResolutionX = resolutionX / 2;
        int halfResolutionY = resolutionY / 2;

        // Calculate correct starting position
        if (!noXScroll)
        {
        if (activePlayer.getXLocation() > (halfResolutionX + maxShiftX))
        {
            shiftX = maxShiftX;
            activePlayer.adjustX(-activePlayer.getXLocation());
        }
        else if (activePlayer.getXLocation() > halfResolutionX)
        {
            shiftX = activePlayer.getXLocation() - halfResolutionX;
            activePlayer.adjustX(-shiftX);
        }
        }

        if (!noYScroll)
        {
        if (activePlayer.getYLocation() > (halfResolutionY + maxShiftY))
        {
            shiftY = maxShiftY;
            activePlayer.adjustY(-shiftY);
        }
        else if (activePlayer.getYLocation() > halfResolutionY)
        {
            shiftY = activePlayer.getYLocation() - halfResolutionY;
            activePlayer.adjustY(-shiftY);
        }
        }

        sprites = activeBoard.getSprites();

        boardLayers = new BufferedImage[activeBoard.getLayers()];
        for (int i = 0; i < boardLayers.length; i++)
        {
            boardLayers[i] = new BufferedImage(activeBoard.getWidth() * 32, activeBoard.getHeight() * 32, BufferedImage.TYPE_INT_ARGB);
        }

        boardChanged = true;

        try
        {
            vgm = new VGMPlayer(22000);

            String fileName = activePlayer.getName().equals("Bowie") ? "http://piglet.co/town.vgm" : "http://piglet.co/derp.vgm";
            vgm.loadFile(fileName, fileName);
            System.out.println(vgm.getVolume());
            System.out.println(vgm.getTrackCount());
            //vgm.play();
            vgm.startTrack(1,1000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Switches the active player in the uk.co.tkce.engine, primarily used during uk.co.tkce.engine start-up, can also
     * be used if the game requires a switch of main character during game play.
     *
     * @param newPlayer Player file for the new character to use.
     */
    public void switchPlayer(Player newPlayer)
    {
        activePlayer = newPlayer;

        // get players vector
        collisionVector = activePlayer.getBaseVector();
        activationVector = activePlayer.getActivationVector();

        activePlayer.loadAnimations();
        activePlayer.setActiveAnimation(0);
        playerImage = activePlayer.getAnimationFrame();

        activePlayer.preparePhysics(playerImage.getWidth(), playerImage.getHeight());
    }

    private class KeyInputHandler extends KeyAdapter
    {
        public void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
            {
                leftPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            {
                rightPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP)
            {
                upPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN)
            {
                downPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                if (spacePressCount < 1)
                {
                    try
                    {
                        testSpriteMap.add(new TestSpriteObject(activePlayer.getXLocation() + 55, activePlayer.getYLocation() + 28));
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    spacePressed = true;
                    spacePressCount ++;
                }

            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                System.exit(0);
            }
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                // Menu
                drawMenu = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_V)
            {
                if (pressCount < 1)
                {
                    vPressed = !vPressed;
                    pressCount++;
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_F12)
            {
                control.pauseGame();
            }

            if (e.getKeyCode() == KeyEvent.VK_F)
            {
                fPressed = !fPressed;
            }

            if (e.getKeyCode() == KeyEvent.VK_D)
            {
                if (dPressCount < 1)
                {
                    dPressed = !dPressed;
                    dPressCount++;
                }
            }
        }

        public void keyReleased(KeyEvent e)
        {
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
            {
                leftPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            {
                rightPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP)
            {
                upPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN)
            {
                downPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_V)
            {
                pressCount--;
            }
            if (e.getKeyCode() == KeyEvent.VK_D)
            {
                dPressCount--;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                spacePressCount --;
                shotFired = false;
            }
        }

        public void keyTyped(KeyEvent e)
        {

        }
    }

    /**
     * Renders the next frame.
     */
    public void renderFrame()
    {
        // Enable AA for vectors
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Draw background image first
        for (BoardImage backgroundImage : activeBoard.getBackgroundImages())
        {
            g.drawImage(backgroundImage.getAsImage(), (-shiftX / backgroundImage.getScrollRatio()), (-shiftY / backgroundImage.getScrollRatio()), width * 32, height * 32, null);
        }

        for (int w = 0; w < layers; w++)
        {
            if (boardChanged)
            {
                for (int x = 0; x < activeBoard.getWidth(); x++)
                {
                    for (int y = 0; y < activeBoard.getHeight(); y++)
                    {
                        int indexToPaint = activeBoard.getIndexAtLocation(x, y, w) - 1;
                        if (indexToPaint >= 0)
                        {
                            boardLayers[w].getGraphics().drawImage(activeBoard.getTileFromIndex(indexToPaint).getTileAsImage(), (x * 32), (y * 32), null);
                        }
                    }
                }
                if (w == (layers - 1)) boardChanged = false;
            }

            g.drawImage(boardLayers[w], 0 - shiftX, 0 - shiftY, null);

            if (w == activeBoard.getStartingLayer() - 1)
            {
                g.drawImage(playerImage, activePlayer.getXLocation(), activePlayer.getYLocation(), null);
            }

            for (BoardSprite sprite : sprites)
            {
                g.drawImage(sprite.getAnimationFrame(), (int) sprite.getX() - shiftX - (sprite.getWidth() / 2), (int) sprite.getY() - shiftY - sprite.getHeight(), null);
            }
        }

        for (TestSpriteObject image : testSpriteMap)
        {
            if (image.getX() > resolutionX) testSpriteMap.remove(image);
            g.drawImage(image.image, image.getX(), image.getY(),null);
        }

        // Pass the graphics object to the debugging tools, comment this line out for release
        debugTools(g);

        // Show the new frame by flipping the buffer
        strategy.show();
    }

    private void debugTools(Graphics2D g)
    {

        if (fPressed)
        {
            g.drawImage(new MessageWindowLibrary("Good Morning!").getMessageWindowToRender(), 41, 327, null);
        }

        // Show debug information (x,y,fps) if D is pressed
        if (dPressed)
        {
                  // Draw debug information
            g.setColor(Color.WHITE);

            g.drawImage(overlayImage, 0, 0, null);

            g.drawString("Toolkit CE (v1.0.0-svn)", 30, 30);
            g.drawString("FPS: " + lastFPS, 30, 50);
            g.drawString("render x: " + activePlayer.getXLocation() + " y: " + activePlayer.getYLocation(), 250, 30);
            g.drawString("actual x: " + (activePlayer.getXLocation() + shiftX) + " y: " + (activePlayer.getYLocation() + shiftY), 250, 50);
            g.drawString("shift  x: " + shiftX + " y: " + shiftY, 250, 70);
            g.drawString("full screen: " + fullScreen, 30, 70);

            // Calculates estimated FPS
            if ((System.currentTimeMillis() - firstFrame) > 1000)
            {
                lastFPS = fps;
                fps = 0;
                firstFrame = System.currentTimeMillis();
            }
            else
            {
                fps++;
            }
        }
    }

    /**
     * Method to check input from the user, this code will support game pads in the future.
     */
    public void checkLogic()
    {
        if(physicsEngine.checkItemActivations(Player.DIRECTION_NORTH, shiftX, shiftY))
        {
            Board b = new Board(new File(System.getProperty("project.path") + "/boards/home_2.brd"));
            this.switchBoard(b);
            this.preparePhysics();
        }

        playerImage = activePlayer.getAnimationFrame();

        if (upPressed && !downPressed)
        {
            if (currentDirection != Player.DIRECTION_NORTH)
            {
                if ((!leftPressed) && (!rightPressed))
                {
                    activePlayer.setActiveAnimation(Player.DIRECTION_NORTH);
                    currentDirection = Player.DIRECTION_NORTH;
                }
            }

            playerImage = activePlayer.getAnimationFrame();

            if (!physicsEngine.checkCollision(Player.DIRECTION_NORTH, shiftX, shiftY))
            {
                if (activePlayer.getYLocation() >= resolutionY / 2)
                {
                    activePlayer.adjustY(-playerMove);
                }
                else
                {
                    if (shiftY > 0)
                    {
                        shiftY -= playerMove;
                    }
                    else
                    {
                        activePlayer.adjustY(-playerMove);
                    }
                }

            }

        }
        else if (downPressed && !upPressed)
        {
            if (currentDirection != Player.DIRECTION_SOUTH)
            {
                if ((!leftPressed) && (!rightPressed))
                {
                    activePlayer.setActiveAnimation(Player.DIRECTION_SOUTH);
                    currentDirection = Player.DIRECTION_SOUTH;
                }
            }

            playerImage = activePlayer.getAnimationFrame();

            if (!physicsEngine.checkCollision(Player.DIRECTION_SOUTH, shiftX, shiftY))
            {
                if (activePlayer.getYLocation() <= resolutionY / 2)
                {
                    activePlayer.adjustY(playerMove);
                }
                else
                {
                    int maxShift = (height * 32) - resolutionY;
                    if (shiftY < maxShift)
                    {
                        shiftY += playerMove;
                    }
                    else
                    {
                        activePlayer.adjustY(playerMove);
                    }
                }
            }
        }

        if (leftPressed && !rightPressed)
        {
            if (currentDirection != Player.DIRECTION_WEST)
            {
                activePlayer.setActiveAnimation(Player.DIRECTION_WEST);
                currentDirection = Player.DIRECTION_WEST;
            }

            playerImage = activePlayer.getAnimationFrame();

            if (!physicsEngine.checkCollision(Player.DIRECTION_WEST, shiftX, shiftY))
            {
                if (activePlayer.getXLocation() >= resolutionX / 2)
                {
                    activePlayer.adjustX(-playerMove);
                }
                else
                {
                    if (shiftX > 0)
                    {
                        shiftX -= playerMove;
                    }
                    else
                    {
                        activePlayer.adjustX(-playerMove);

                    }
                }
            }

        }
        else if (rightPressed && !leftPressed)
        {
            if (currentDirection != Player.DIRECTION_EAST)
            {
                activePlayer.setActiveAnimation(Player.DIRECTION_EAST);
                currentDirection = Player.DIRECTION_EAST;
            }

            playerImage = activePlayer.getAnimationFrame();

            if (!physicsEngine.checkCollision(Player.DIRECTION_EAST, shiftX, shiftY))
            {
                if (activePlayer.getXLocation() <= resolutionX / 2)
                {
                    activePlayer.adjustX(playerMove);
                }
                else
                {
                    int maxShift = (width * 32) - resolutionX;
                    if (shiftX < maxShift)
                    {
                        shiftX += playerMove;
                    }
                    else
                    {
                        activePlayer.adjustX(playerMove);
                    }
                }
            }
        }
    }

    private void logic()
    {
        float dPadPosition = xbcs.getDPadPosition();

        rightPressed = dPadPosition == Component.POV.RIGHT || dPadPosition == Component.POV.UP_RIGHT ||
                dPadPosition == Component.POV.DOWN_RIGHT || rightPressed;

        leftPressed = dPadPosition == Component.POV.LEFT || dPadPosition == Component.POV.UP_LEFT ||
                dPadPosition == Component.POV.DOWN_LEFT || leftPressed;

        upPressed = dPadPosition == Component.POV.UP || dPadPosition == Component.POV.UP_LEFT ||
                dPadPosition == Component.POV.UP_RIGHT || upPressed;

        downPressed = dPadPosition == Component.POV.DOWN ||dPadPosition == Component.POV.DOWN_LEFT ||
                dPadPosition == Component.POV.DOWN_RIGHT || downPressed;

        activePlayer.isMoving(rightPressed || leftPressed || upPressed || downPressed || true);
    }

    public void begin()
    {
        double lastFrameTime = System.currentTimeMillis();
        double targetFPS = 1000 / 60; // 60 fps
        while (!finished)
        {
            if (!fullScreen)
            {
                if(lastFrameTime + targetFPS < System.currentTimeMillis())
                {
                    this.logic();
                    this.checkLogic();
                    lastFrameTime = System.currentTimeMillis();
                }
            }
            else
            {
                if (!fPressed)
                {
                this.logic();
                this.checkLogic();
                }
            }

            this.renderFrame();


        }

    }

}

