package uk.co.tkce.toolkit.test;

import uk.co.tkce.toolkit.exceptions.TilePixelOutOfRangeException;
import uk.co.tkce.toolkit.types.Board;
import uk.co.tkce.toolkit.types.TKVector;
import uk.co.tkce.toolkit.types.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BoardCanvas extends JPanel
{
    private Board board;
    private BufferedImage bi;

    public BoardCanvas(Board board)
    {
        super();
        this.board = board;

        bi = new BufferedImage(board.getWidth() * 32, board.getHeight() * 32, BufferedImage.TYPE_INT_ARGB);
        try
        {
            this.testPaint();
        }
        catch (TilePixelOutOfRangeException e)
        {
            e.printStackTrace();
        }
        this.repaint();
    }

    @Override
    public Dimension getPreferredSize()
    {
        return createPreferredSize();
    }

    private Dimension createPreferredSize()
    {
        return new Dimension(board.getWidth() * 32, board.getHeight() * 32);
    }

    public void paint(Graphics g)
    {
        g.drawImage(bi, 0, 0, this);
    }

    public void testPaint() throws TilePixelOutOfRangeException
    {
        Graphics2D g = bi.createGraphics();

        int layers = board.getLayers();
        int width = board.getWidth();
        int height = board.getHeight();

        // Draw background image first

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, board.getWidth(), board.getHeight());

        // This Line doesn't work anymore!
        //if (!board.getBackground().equals("")) g.drawImage(board.getBackgroundImages(), 0, 0, width*32, height*32, this);

        for (int w = 0; w < layers; w++)
        {
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    int indexToPaint = board.getIndexAtLocation(x, y, w) - 1;
                    if (indexToPaint >= 0)
                    {
                        Tile tile = board.getTileFromIndex(indexToPaint);

                        g.drawImage(tile.getTileAsImage(), (x * 32), (y * 32), this);

                    }
                    else
                    {
                        g.setColor(Color.BLACK);
                    }
                }
            }
        }

        // Draw Vectors
        ArrayList<TKVector> vectors = board.getVectors();
        for (TKVector vector : vectors)
        {
            // Draw lines from points 0 > 1 , 1 > 2, 2 > 3 etc..
            int count = vector.getPointCount();
            switch (vector.getTileType())
            {
                case 1:
                    g.setColor(Color.WHITE);
                    break;
                case 2:
                    g.setColor(Color.GREEN);
                    break;
                case 16:
                    g.setColor(Color.RED);
                    break;
                default:
                    g.setColor(Color.WHITE);
            }

            for (int i = 0; i < count - 1; i++)
            {
                g.drawLine(vector.getPointX(i), vector.getPointY(i), vector.getPointX(i + 1), vector.getPointY(i + 1));
            }

            // Draw the final lines
            g.drawLine(vector.getPointX(count - 1), vector.getPointY(count - 1), vector.getPointX(0), vector.getPointY(0));
        }
    }
}
