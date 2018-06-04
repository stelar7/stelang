package ast.exprs;

import ast.exprs.Expression;

public class VariableDefinitionExpression extends Expression
{
    private String     identifier;
    private String     visibility;
    private Expression value;
    
    public VariableDefinitionExpression(String identifier, String visibility, Expression value)
    {
        this.identifier = identifier;
        this.visibility = visibility;
        this.value = value;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
