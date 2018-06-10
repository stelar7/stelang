package ast.exprs.basic;

import ast.exprs.Expression;

public class LongExpression extends Expression
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
