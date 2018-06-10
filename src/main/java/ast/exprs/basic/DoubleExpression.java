package ast.exprs.basic;

import ast.exprs.Expression;

public class DoubleExpression extends Expression
{
    private double val;
    
    public DoubleExpression(double val)
    {
        this.val = val;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
