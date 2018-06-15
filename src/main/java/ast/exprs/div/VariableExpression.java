package ast.exprs.div;

import ast.exprs.Expression;

public class VariableExpression implements Expression
{
    private String name;
    
    public VariableExpression(String name)
    {
        this.name = name;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
