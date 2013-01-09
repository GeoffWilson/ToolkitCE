package uk.co.tkce.engine;

import uk.co.tkce.toolkit.types.Board;
import uk.co.tkce.toolkit.types.BoardProgram;
import uk.co.tkce.toolkit.types.Player;
import uk.co.tkce.toolkit.types.TKVector;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;

public class PhysicsEngine
{
    private int delta;

    private ArrayList<Area> collisionShapes;
    private ArrayList<Area> itemShapes;

    private Player activePlayer;

    private int correctX;
    private int correctY;

    public PhysicsEngine()
    {

    }

    public void setCollisionDelta(int delta)
    {
        this.delta = delta;
    }

    public void setActivePlayer(Player newPlayer)
    {
        this.activePlayer = newPlayer;
    }

    public void loadNewBoardVectors(Board newBoard)
    {
        collisionShapes = new ArrayList<>();
        itemShapes = new ArrayList<>();

        for (TKVector vector : newBoard.getVectors())
        {
            Polygon newShape = new Polygon();
            for (Point point : vector.getPoints())
            {
                newShape.addPoint(point.x, point.y);
            }
            if (vector.getTileType() == 1) collisionShapes.add(new Area(newShape));
        }

        for (BoardProgram program : newBoard.getPrograms())
        {
            Polygon newShape = new Polygon();
            TKVector vector = program.getVector();
            for (Point point : vector.getPoints())
            {
                newShape.addPoint(point.x, point.y);
            }

            itemShapes.add(new Area(newShape));
        }
    }

    /**
     * Checks to see if the players vector has collided with any of the board vectors,
     * (actually checks if the player *will* collide if moved)
     *
     * @param direction Need to know the direction to correctly calculate collisions
     * @param shiftX    Temporary value - will be extracted into Player
     * @param shiftY    Temporary value - will be extracted into Player
     * @return true if will collide, false if not.
     */
    public boolean checkCollision(int direction, int shiftX, int shiftY)
    {
        calculateDifferences(direction);

        for (Area shape : collisionShapes)
        {
            Area newShape = (Area) shape.clone();
            newShape.intersect(activePlayer.getCollisionArea(correctX, correctY, shiftX, shiftY));

            if (!newShape.isEmpty())
            {
                return true;
            }
        }

        return false;
    }

    public boolean checkItemActivations(int direction, int shiftX, int shiftY)
    {

        calculateDifferences(direction);

        // Check item collisions
        for (Area shape : itemShapes)
        {
            Area newShape = (Area) shape.clone();
            newShape.intersect(activePlayer.getCollisionArea(correctX, correctY, shiftX, shiftY));

            if (!newShape.isEmpty())
            {
                return true;
            }
        }

        //return false;
    }

    private void calculateDifferences(int direction)
    {
        switch (direction)
        {
            case Player.DIRECTION_NORTH:
                correctX = activePlayer.getVectorCorrectionX();
                correctY = activePlayer.getVectorCorrectionY() - delta;
                break;
            case Player.DIRECTION_SOUTH:
                correctX = activePlayer.getVectorCorrectionX();
                correctY = activePlayer.getVectorCorrectionY() + delta;
                break;
            case Player.DIRECTION_EAST:
                correctX = activePlayer.getVectorCorrectionX() + delta;
                correctY = activePlayer.getVectorCorrectionY();
                break;
            case Player.DIRECTION_WEST:
                correctX = activePlayer.getVectorCorrectionX() - delta;
                correctY = activePlayer.getVectorCorrectionY();
                break;
        }
    }
}
