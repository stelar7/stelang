package ast.exprs.control;

public class PrototypeParameter
{
    private String type;
    private String name;
    
    public PrototypeParameter(String type, String name)
    {
        this.type = type;
        this.name = name;
    }
    
    public String getType()
    {
        return type;
    }
    
    public String getName()
    {
        return name;
    }
}
