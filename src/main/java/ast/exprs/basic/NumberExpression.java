package ast.exprs.basic;

import ast.exprs.Expression;

public class NumberExpression extends Expression
{
    private double val;
    
    public NumberExpression(double val)
    {
        this.val = val;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
