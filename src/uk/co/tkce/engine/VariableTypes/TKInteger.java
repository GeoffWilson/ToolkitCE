package uk.co.tkce.engine.VariableTypes;

/**
 * Defines a variable of type Integer
 *
 * @author Geoff Wilson
 * @version svn
 */
public class TKInteger implements BaseVariable
{

    private String name;
    private Integer value;

    /**
     * Declares and implements an integer variable
     *
     * @param name          The variables name
     * @param startingValue Initial value for the variable
     */
    public TKInteger(String name, Integer startingValue)
    {
        this.name = name;
        this.value = startingValue;
    }

    public void setValue(Object newValue)
    {
        if (newValue instanceof Integer)
        {
            value = (Integer) newValue;
        }
        else
        {
            // should not be able to get here, exception checking should happen before this
            System.out.println("EXCEPTION : Type mismatch");
        }
    }

    public Object getValue()
    {
        return value;
    }

    // Should not really rename a variable
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
}
