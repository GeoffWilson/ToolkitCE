package uk.co.tkce.engine;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Version;

/**
 * Provides support for the standard 360 controller
 * A = 0
 * B = 1
 * X = 2
 * Y = 3
 */

public class XboxControllerSupport
{
    private final int A_BUTTON = 0;
    private final int B_BUTTON = 1;
    private final int X_BUTTON = 2;
    private final int Y_BUTTON = 3;
    private final int START_BUTTON = 7;
    private final int BACK_BUTTON = 6;
    private Controller xboxController;
    private Component dPad;
    private Component[] buttons;

    public XboxControllerSupport()
    {
        System.out.println("JInput version: " + Version.getVersion());

        xboxController = getXboxController();
        if (xboxController == null)
        {
            System.out.println("No Xbox controller detected, disabling game pad support");
        }
        else
        {
            System.out.println("360 Controller Found");

            dPad = xboxController.getComponent(Component.Identifier.Axis.POV);
            buttons = new Component[10];
            buttons[A_BUTTON] = xboxController.getComponent(Component.Identifier.Button._0);
            buttons[B_BUTTON] = xboxController.getComponent(Component.Identifier.Button._1);
            buttons[X_BUTTON] = xboxController.getComponent(Component.Identifier.Button._2);
            buttons[Y_BUTTON] = xboxController.getComponent(Component.Identifier.Button._3);
            buttons[START_BUTTON] = xboxController.getComponent(Component.Identifier.Button._7);
            buttons[BACK_BUTTON] = xboxController.getComponent(Component.Identifier.Button._6);
        }
    }

    private Controller getXboxController()
    {
        // Detect if we have an Xbox controller present

        System.out.println("Checking Controller Support....");
        ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
        Controller[] cs = ce.getControllers();

        for (Controller c : cs)
        {
            if (c.getName().contains("XBOX 360"))
            {
                if (c.getType() == Controller.Type.GAMEPAD)
                {
                    return c;
                }
            }
        }

        return null;
    }

    public float getDPadPosition()
    {
        if (xboxController != null)
        {
            xboxController.poll();
            return dPad.getPollData();
        }
        return 0.0f;
    }

    public float backPressed()
    {
        if (xboxController != null)
        {
            xboxController.poll();
            return buttons[BACK_BUTTON].getPollData();
        }
        return 0.0f;
    }
}
