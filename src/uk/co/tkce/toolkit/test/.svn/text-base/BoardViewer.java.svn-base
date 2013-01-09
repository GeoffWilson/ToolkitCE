package uk.co.tkce.toolkit.test;

import uk.co.tkce.toolkit.editor.MainWindow;
import uk.co.tkce.toolkit.types.Board;

import javax.swing.*;
import java.io.File;

public class BoardViewer extends JInternalFrame
{
    private MainWindow parent;

    private Board board;
    private BoardCanvas canvas;
    private JScrollPane scroll;

    public BoardViewer()
    {
        setupWindow();
    }

    public BoardViewer(File fileName)
    {
        super("Board Viewer", true, true, true, true);
        setupWindow();
        board = new Board(fileName);
        canvas = new BoardCanvas(board);
        canvas.setPreferredSize(canvas.getPreferredSize());
        this.setTitle("Viewing " + fileName.getAbsolutePath());
        this.scroll = new JScrollPane(canvas);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scroll);

    }

    private void setupWindow()
    {
        this.setSize(800, 600);
        this.setVisible(true);
    }
}
