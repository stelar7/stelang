package ast.exprs.basic;

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
        return String.format("%%%s", name);
    }
    
    public String getName()
    {
        return name;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
}
