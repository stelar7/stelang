package ast.exprs.div;


import ast.exprs.Expression;

public class VariableDefinitionExpression extends Expression
{
    private String identifier;
    private String visibility;
    
    public VariableDefinitionExpression(String identifier, String visibility)
    {
        this.identifier = identifier;
        this.visibility = visibility;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
