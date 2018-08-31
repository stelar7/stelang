package ast.exprs.basic;

import ast.exprs.Expression;

public class NumberExpression implements Expression
{
    private long val;
    
    public NumberExpression(long val)
    {
        this.val = val;
    }
    
    @Override
    public String codegen()
    {
        return "i64 " + val;
    }
    
    @Override
    public String toString()
    {
        return codegen();
    }
}
