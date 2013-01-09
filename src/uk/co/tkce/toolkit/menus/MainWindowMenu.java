package uk.co.tkce.toolkit.menus;

import uk.co.tkce.toolkit.editor.MainWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindowMenu extends JMenuBar
{
    private final MainWindow parent;

    private JMenu fileMenu;
    private JMenuItem newProjectMenuItem;
    private JMenuItem openProjectMenuItem;
    private JMenuItem saveAllMenuItem;
    private JMenuItem exitMenuItem;

    private JMenu objectMenu;
    private JMenu newObjectMenu;
    private JMenuItem newAnimationMenuItem;
    private JMenuItem newItemMenuItem;
    private JMenuItem newTileMenuItem;
    private JMenuItem newProgramMenuItem;
    private JMenu openObjectMenu;
    private JMenuItem openBoardMenuItem;
    private JMenuItem openAnimationMenuItem;
    private JMenuItem openBackgroundMenuItem;
    private JMenuItem openCharMenuItem;
    private JMenuItem openItemMenuItem;
    private JMenuItem openProgramMenuItem;
    private JMenuItem openStatusEffectMenuItem;
    private JMenuItem openSpecialMoveMenuItem;
    private JMenuItem openTilesetMenuItem;

    private JMenu toolsMenu;
    private JMenuItem boardViewerMenuItem;
    private JMenuItem tilesetViewerMenuItem;

    private JMenu helpMenu;
    private JMenuItem indexMenuItem;
    private JMenuItem aboutMenuItem;

    public MainWindowMenu(MainWindow menuParent)
    {
        super();

        this.parent = menuParent;

        this.configureFileMenu();
        this.configureObjectMenu();
        this.configureToolMenu();
        this.configureHelpMenu();

        this.add(fileMenu);
        this.add(objectMenu);
        this.add(toolsMenu);
        this.add(helpMenu);
    }

    private void configureFileMenu()
    {
        fileMenu = new JMenu("File");

        newProjectMenuItem = new JMenuItem("New Project");
        newProjectMenuItem.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/menu_new.png")));
        newProjectMenuItem.setEnabled(false);
        newProjectMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.newProject();
            }
        });

        openProjectMenuItem = new JMenuItem("Open Project");
        openProjectMenuItem.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/menu_open.png")));
        openProjectMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.openProject();
            }
        });

        saveAllMenuItem = new JMenuItem("Save All");
        saveAllMenuItem.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/menu_save.png")));
        saveAllMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.saveAll();
            }
        });

        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/menu_exit.png")));
        exitMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });

        fileMenu.add(newProjectMenuItem);
        fileMenu.add(openProjectMenuItem);
        fileMenu.add(saveAllMenuItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitMenuItem);

    }

    private void configureObjectMenu()
    {
        objectMenu = new JMenu("Objects");

        newObjectMenu = new JMenu("New");
        newObjectMenu.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/menu_new.png")));

        openObjectMenu = new JMenu("Open");
        openObjectMenu.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/menu_open.png")));

        this.configureNewSubMenu();
        this.configureOpenSubMenuMenu();

        objectMenu.add(newObjectMenu);
        objectMenu.add(openObjectMenu);

    }

    private void configureNewSubMenu()
    {
        newAnimationMenuItem = new JMenuItem("Animation");
        newAnimationMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.newAnimation();
            }
        });

        newItemMenuItem = new JMenuItem("Item");
        newItemMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.newItem();
            }
        });

        newTileMenuItem = new JMenuItem("Tile");

        newProgramMenuItem = new JMenuItem("Program");
        newProgramMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.newProgram();
            }
        });

        // Add Sub Menus in ALPHABETICAL ORDER
        newObjectMenu.add(newAnimationMenuItem);
        newObjectMenu.add(newItemMenuItem);
        newObjectMenu.add(newProgramMenuItem);
        newObjectMenu.add(newTileMenuItem);
    }

    private void configureOpenSubMenuMenu()
    {
        openAnimationMenuItem = new JMenuItem("Animation");
        openAnimationMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.openAnimation();
            }
        });

        openBoardMenuItem = new JMenuItem("Board");
        openBoardMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.openBoard();
            }
        });

        openItemMenuItem = new JMenuItem("Item");
        openItemMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.openItem();
            }
        });

        openCharMenuItem = new JMenuItem("Player");
        openCharMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.openCharacter();
            }
        });

        openProgramMenuItem = new JMenuItem("Program");
        openProgramMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.openProgram();
            }
        });

        openSpecialMoveMenuItem = new JMenuItem("Special Move");
        openSpecialMoveMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.openSpecialMove();
            }
        });

        openStatusEffectMenuItem = new JMenuItem("Status Effect");
        openStatusEffectMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.openStatusEffect();
            }
        });

        openTilesetMenuItem = new JMenuItem("TileSet");
        openTilesetMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.openTileset();
            }
        });

        // Add Sub Menus in ALPHABETICAL ORDER
        openObjectMenu.add(openAnimationMenuItem);      // ANIMATION        .ANM
        openObjectMenu.add(openBoardMenuItem);          // BOARD            .BRD
        openObjectMenu.add(openItemMenuItem);           // ITEM             .ITM
        openObjectMenu.add(openCharMenuItem);           // PLAYER           .TEM
        openObjectMenu.add(openProgramMenuItem);        // PROGRAM          .PRG
        openObjectMenu.add(openSpecialMoveMenuItem);    // SPECIAL MOVE     .SPC
        openObjectMenu.add(openStatusEffectMenuItem);   // STATUS EFFECT    .STE
        openObjectMenu.add(openTilesetMenuItem);        // TILESET          .TST
    }

    private void configureToolMenu()
    {
        toolsMenu = new JMenu("Tools");

        tilesetViewerMenuItem = new JMenuItem("Tileset Viewer");
        tilesetViewerMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.openTilesetForView();
            }
        });

        boardViewerMenuItem = new JMenuItem("Board Viewer");
        boardViewerMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.openBoardForView();
            }
        });

        toolsMenu.add(boardViewerMenuItem);
        toolsMenu.add(tilesetViewerMenuItem);
    }

    private void configureHelpMenu()
    {
        helpMenu = new JMenu("Help");

        indexMenuItem = new JMenuItem("Index");     // Help Index Menu (browser based?)
        indexMenuItem.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/menu_help.png")));
        indexMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.showHelpMenu();
            }
        });

        aboutMenuItem = new JMenuItem("About");     // About Menu
        aboutMenuItem.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/menu_about.png")));
        aboutMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.showAbout();

            }
        });

        helpMenu.add(indexMenuItem);
        helpMenu.add(aboutMenuItem);
    }
}


