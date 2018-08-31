package ast.exprs.basic;

import ast.exprs.Expression;

public class FloatExpression implements Expression
{
    private double val;
    
    public FloatExpression(double val)
    {
        this.val = val;
    }
    
    @Override
    public String codegen()
    {
        return "double " + val;
    }
}
