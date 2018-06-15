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
        return null;
    }
    
}
