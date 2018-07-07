package ast.exprs.basic;

import ast.exprs.Expression;

public class LongExpression implements Expression
{
    private long val;
    
    public LongExpression(long val)
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
