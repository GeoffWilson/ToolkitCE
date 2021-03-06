package uk.co.tkce.engine.InternalObjects;

import uk.co.tkce.toolkit.types.Project;

import java.util.HashMap;

/**
 * This is the "GFX" TransCE internal object, this is responsible for
 * allowing users to render 2D shapes and images to virtual canvas'
 *
 * @author Geoff Wilson
 * @version 1.0
 */
public class GraphicsLibrary
{
    private HashMap<Integer, TransCanvas> canvasMap;

    public GraphicsLibrary(Project projectFile)
    {

    }

    public void executeCommand(RawCommand command)
    {
        switch (command.getFunction())
        {
            case "CREATECANVAS":
                createNewCanvas(command.getArgAsInt(0));
                break;
            case "DELETECANVAS":
                deleteCanvas(0);
                break;
            case "RENDERCANVAS":
                canvasMap.get(0).getCanvasToRender(); // add this object to the master rendering queue.
                break;
            case "DRAWLINE":
                canvasMap.get(0).drawLine(0,0,100,100);
                break;
            default:
                // unknown command
        }
    }

    private void createNewCanvas(int id)
    {
        canvasMap.put(id, new TransCanvas((int) 800, (int) 600));
    }

    private void deleteCanvas(int id)
    {
        canvasMap.remove(id);
    }
}
