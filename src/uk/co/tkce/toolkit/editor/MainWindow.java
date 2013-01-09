package uk.co.tkce.toolkit.editor;

import uk.co.tkce.engine.GameControl;
import uk.co.tkce.toolkit.menus.MainWindowMenu;
import uk.co.tkce.toolkit.test.*;
import uk.co.tkce.toolkit.types.Animation;
import uk.co.tkce.toolkit.types.Board;
import uk.co.tkce.toolkit.types.Project;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class MainWindow extends JFrame
{
    private TileEditor tileEditor;
    private JDesktopPane desktopPane;
    private JPanel debugPane;
    private JTextField debugLog;
    private Project activeProject;
    private MainToolBar toolBar;
    private JFileChooser fileChooser;
    private final String workingDir = System.getProperty("user.dir");
    private ArrayList<ToolkitEditorWindow> activeWindows;

    public MainWindow()
    {
        super("Community Toolkit");

        activeWindows = new ArrayList<ToolkitEditorWindow>();

        class DesktopPaneWithBackground extends JDesktopPane
        {
            public DesktopPaneWithBackground()
            {
                super();
            }

            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                //ImageIcon icon = new ImageIcon(getClass().getResource("/uk.co.tkce.toolkit/assets/palm.png"));
                // g.drawImage(icon.getImage(),0,0,this);
            }
        }

        desktopPane = new DesktopPaneWithBackground();
        desktopPane.setDesktopManager(new ToolkitDesktopManager(this));
        desktopPane.setBackground(Color.LIGHT_GRAY);

        ImageIcon icon = new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/application.png"));
        this.setIconImage(icon.getImage());

        debugPane = new JPanel();
        debugLog = new JTextField("Debug Messages:");
        debugLog.setEditable(false);
        debugLog.setFocusable(false);

        debugLog.setText(System.getProperty("user.dir"));

        debugPane.setLayout(new BorderLayout());
        debugPane.add(debugLog, BorderLayout.CENTER);

        this.setLayout(new BorderLayout());

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        toolBar = new MainToolBar(this);

        JPanel fileBrowser = new JPanel();
        fileBrowser.setPreferredSize(new Dimension(150, 10));
        fileBrowser.setMaximumSize(new Dimension(175, 10));
        fileBrowser.setLayout(new BorderLayout());
        DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode("NULL");
        JTree files = new JTree(projectNode);

        fileBrowser.add(files, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileBrowser, desktopPane);
        splitPane.setDividerSize(10);

        this.setSize(new Dimension(1024, 768));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(new MainWindowMenu(this));

        this.add(toolBar, BorderLayout.PAGE_START);
        this.add(splitPane, BorderLayout.CENTER);
        this.add(debugPane, BorderLayout.PAGE_END);
        this.setLocationByPlatform(true);
        this.setVisible(true);
    }

    public void runGame()
    {
        if (activeProject != null)
        {
            GameControl gc = new GameControl(activeProject);
            Thread t = new Thread(gc);
            t.start();
        }
    }

    public void newProject()
    {
        ProjectEditor pe = new ProjectEditor();
        desktopPane.add(pe, BorderLayout.CENTER);
    }

    public void openProject()
    {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Toolkit Project", "gam");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            activeProject = new Project(fileChooser.getSelectedFile());
            ProjectEditor pe = new ProjectEditor(activeProject);
            this.setTitle(this.getTitle() + " - " + activeProject.getGameTitle() + " project loaded");
            desktopPane.add(pe, BorderLayout.CENTER);
            pe.setWindowParent(this);
            activeWindows.add(pe);
            toolBar.enableRun();
        }
    }

    public void newProgram()
    {
        ProgramEditor testCodeEditor = new ProgramEditor();
        desktopPane.add(testCodeEditor);
    }

    public void openProgram()
    {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("RPG Code Program", "prg");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            ProgramEditor testCodeEditor = new ProgramEditor();
            testCodeEditor.open(fileChooser.getSelectedFile());
            desktopPane.add(testCodeEditor);
        }
    }

    /**
     * Creates a new animation editor window
     */
    public void newAnimation()
    {

    }

    /**
     * Creates an animation editor window for modifying the specified animation file.
     */
    public void openAnimation()
    {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Animation", "anm");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            Animation animation = new Animation(fileChooser.getSelectedFile());
            AnimationEditor animEditor = new AnimationEditor(animation);
            desktopPane.add(animEditor);
        }
    }

    public void openBoard()
    {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Board", "brd");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            Board testBoard = new Board(fileChooser.getSelectedFile());

        }
    }

    /**
     * Creates a new item editor window
     */
    public void newItem()
    {

    }

    /**
     * Creates an item editor window for modifying the specified item file.
     */
    public void openItem()
    {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Item", "itm");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {

        }
    }

    /**
     * Creates a new Tile (Tileset) editor window
     */
    public void newTileset()
    {

    }

    /**
     * Creates a tileset editor window for modifying the specified tilset
     */
    public void openTileset()
    {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Standard Tileset", "tst");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            TileEditor testTileEditor = new TileEditor(fileChooser.getSelectedFile());
            desktopPane.add(testTileEditor);
        }
    }

    public void openTilesetForView()
    {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Standard Tileset", "tst");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            TilesetViewer testTileEditor = new TilesetViewer(fileChooser.getSelectedFile());
            desktopPane.add(testTileEditor);
        }
    }

    public void openBoardForView()
    {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Board", "brd");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            BoardViewer testBoardEditor = new BoardViewer(fileChooser.getSelectedFile());
            desktopPane.add(testBoardEditor);
        }
    }

    /**
     * Creates a new character (player) editor window
     */
    public void newCharacter()
    {

    }

    /**
     * Creates an character editor window for modifying the specified character file.
     */
    public void openCharacter()
    {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Player", "tem");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            PlayerEditor testCharEditor = new PlayerEditor();
            testCharEditor.open(fileChooser.getSelectedFile());
            desktopPane.add(testCharEditor);
        }
    }

    public void newSpecialMove()
    {

    }

    public void openSpecialMove()
    {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Special Move", "spc");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            SpecialMoveEditor testSpecialEditor = new SpecialMoveEditor();
            testSpecialEditor.open(fileChooser.getSelectedFile());
            desktopPane.add(testSpecialEditor);
        }
    }

    public void newStatusEffect()
    {

    }

    public void openStatusEffect()
    {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Status Effect", "ste");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            StatusEffectEditor testEffectEditor = new StatusEffectEditor();
            testEffectEditor.open(fileChooser.getSelectedFile());
            desktopPane.add(testEffectEditor);
        }
    }

    public void showHelpMenu()
    {
        HelpViewer helpViewer = new HelpViewer();
        desktopPane.add(helpViewer);
    }

    public void showAbout()
    {
        AboutDialog about = new AboutDialog(this);
    }

    public boolean saveAll()
    {
        for (ToolkitEditorWindow activeWindow : activeWindows)
        {
            System.out.println("Saving: " + activeWindow.toString());
            if (!activeWindow.save())
            {
                System.out.println("Failed to Save All");
                return false;
            }
        }
        return true;
    }

    public void removeActiveWindow(ToolkitEditorWindow window)
    {
        activeWindows.remove(window);
    }
}
