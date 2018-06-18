package ast.exprs.div;


import ast.exprs.Expression;

public class VariableDefinitionExpression implements Expression
{
    private String identifier;
    private String type;
    
    public VariableDefinitionExpression(String identifier, String visibility)
    {
        this.identifier = identifier;
        this.type = visibility;
    }
    
    public String getIdentifier()
    {
        return identifier;
    }
    
    public String getType()
    {
        return type;
    }
    
    @Override
    public String codegen()
    {
        return String.format("%%%s = 0", identifier);
    }
    
    @Override
    public int getSortOrder()
    {
        return 1;
    }
}
