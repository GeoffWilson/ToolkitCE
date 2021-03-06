package uk.co.tkce.engine;

import uk.co.tkce.engine.InternalObjects.GraphicsLibrary;
import uk.co.tkce.engine.InternalObjects.MathLibrary;
import uk.co.tkce.engine.InternalObjects.RawCommand;
import uk.co.tkce.engine.InternalObjects.SystemLibrary;
import uk.co.tkce.engine.VariableTypes.BaseVariable;
import uk.co.tkce.engine.VariableTypes.TKInteger;

import java.util.HashMap;

public class CodeEngine
{
    private GraphicsLibrary graphicsSubsystem;
    private MathLibrary mathSubsystem;
    private SystemLibrary systemSubsystem;
    //private TransIO ioSubsystem;

    // Variables
    private HashMap<String, BaseVariable> variables;

    private String fileName;
    private boolean isReady = false;

    public CodeEngine()
    {
        variables = new HashMap<>();
    }

    public void loadProgram()
    {

    }

    public void executeLine(RawCommand command)
    {
        if (isReady)
        {
            switch (command.getLibrary())
            {
                case "GFX":
                    graphicsSubsystem.executeCommand(command);
                    break;
                case "MATH":

                default:
                    System.out.println("Unknown Command");
            }
        }
        else
        {
            System.out.println("Not Ready to Execute Code");
        }

    }

    public void createInteger(String name, Integer value)
    {
        if (!variables.containsKey(name))
        {
            TKInteger newInt = new TKInteger(name, value);
            variables.put(name, newInt);
        }
        else
        {
            System.out.println("Variable " + name + " is already defined in this scope");
        }
    }

    public void changeInteger(String name, Integer newValue)
    {
        if (variables.containsKey(name))
        {
            BaseVariable tkInt = variables.get(name);
            tkInt.setValue(newValue);
        }
    }
}
