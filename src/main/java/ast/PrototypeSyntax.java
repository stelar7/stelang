package ast;

import java.util.List;

public class PrototypeSyntax
{
    private String                   name;
    private List<PrototypeParameter> parameters;
    
    public PrototypeSyntax(String name, List<PrototypeParameter> parameters)
    {
        this.name = name;
        this.parameters = parameters;
    }
}
