package ast;

import java.util.List;

public class PrototypeSyntax
{
    private String                   name;
    private List<PrototypeParameter> parameters;
    private String                   returnType;
    
    public PrototypeSyntax(String name, List<PrototypeParameter> parameters, String returnType)
    {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
    }
}
