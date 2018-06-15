package ast.exprs.basic;

import ast.exprs.Expression;

public class BooleanExpression implements Expression
{
    
    private boolean result;
    
    public BooleanExpression(boolean result)
    {
        this.result = result;
    }
    
    public boolean isResult()
    {
        return result;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
