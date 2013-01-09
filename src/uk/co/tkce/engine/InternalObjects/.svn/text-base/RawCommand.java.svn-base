package uk.co.tkce.engine.InternalObjects;

import java.util.ArrayList;

public class RawCommand
{
    private String library;
    private String function;
    private ArrayList<Object> args;

    public RawCommand()
    {
        args = new ArrayList<Object>();
    }

    public String getLibrary()
    {
        return library;
    }

    public void setLibrary(String library)
    {
        this.library = library;
    }

    public String getFunction()
    {
        return function;
    }

    public void setFunction(String function)
    {
        this.function = function;
    }

    public void addArg(String varName)
    {
        args.add(varName);
    }

    public int getArgAsInt(int id)
    {
        return (Integer) args.get(id);
    }

    public double getArgsAsDouble(int id)
    {
        return (Double) args.get(id);
    }

    public boolean getArgsAsBoolean(int id)
    {
        return (Boolean) args.get(id);
    }

    public String getArgsAsString(int id)
    {
        return (String) args.get(id);
    }

}
