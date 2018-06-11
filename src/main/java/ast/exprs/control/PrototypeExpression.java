package ast.exprs.control;

import java.util.List;

public class PrototypeExpression
{
    private String                   name;
    private List<PrototypeParameter> parameters;
    private String                   returnType;
    
    public PrototypeExpression(String name, List<PrototypeParameter> parameters, String returnType)
    {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
    }
    
    public String getName()
    {
        return name;
    }
    
    public List<PrototypeParameter> getParameters()
    {
        return parameters;
    }
    
    public String getReturnType()
    {
        return returnType;
    }
}
