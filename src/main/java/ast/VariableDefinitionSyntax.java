package ast;

import ast.exprs.Expression;

public class VariableDefinitionSyntax extends Syntax
{
    private String     identifier;
    private String     visibility;
    private Expression value;
    
    public VariableDefinitionSyntax(String identifier, String visibility, Expression value)
    {
        this.identifier = identifier;
        this.visibility = visibility;
        this.value = value;
    }
    
}
