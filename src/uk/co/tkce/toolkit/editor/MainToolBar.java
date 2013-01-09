package uk.co.tkce.toolkit.editor;

import uk.co.tkce.toolkit.types.EditorButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainToolBar extends JToolBar
{
    private final MainWindow parent;

    private JPopupMenu popupMenu;
    private JMenuItem newAnimationMenu;
    private JMenuItem newProjectMenu;

    private EditorButton newButton;
    private EditorButton openButton;
    private EditorButton saveButton;
    private EditorButton runButton;
    private EditorButton helpButton;

    public MainToolBar(MainWindow mw)
    {
        super();

        this.parent = mw;

        this.setFloatable(true);

        popupMenu = new JPopupMenu();
        newAnimationMenu = new JMenuItem("Animation");
        newProjectMenu = new JMenuItem("Project");
        newProjectMenu.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.newProject();
            }
        });

        popupMenu.add(newAnimationMenu);
        popupMenu.add(newProjectMenu);

        newButton = new EditorButton();
        newButton.setSize(32, 32);
        newButton.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/tb_new.png")));
        newButton.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }

            public void mouseReleased(MouseEvent e)
            {
                popupMenu.setVisible(false);
            }
        });

        this.add(newButton);
        //this.addSeparator();

        openButton = new EditorButton();
        openButton.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/tb_open.png")));
        this.add(openButton);
        //this.addSeparator();

        saveButton = new EditorButton();
        saveButton.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/tb_save.png")));
        saveButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.saveAll();
            }
        });

        this.add(saveButton);
        //this.addSeparator();

        runButton = new EditorButton();
        runButton.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/tb_run.png")));
        runButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                parent.runGame();
            }
        });
        runButton.setEnabled(false);

        this.add(runButton);
//        this.addSeparator();

        helpButton = new EditorButton();
        helpButton.setIcon(new ImageIcon(getClass().getResource("/uk/co/tkce/toolkit/assets/tb_help.png")));
        this.add(helpButton);
    }

    public void enableRun()
    {
        runButton.setEnabled(!runButton.isEnabled());
    }

}
