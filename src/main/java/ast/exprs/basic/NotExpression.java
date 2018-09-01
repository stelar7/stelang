package ast.exprs.basic;

import ast.exprs.Expression;

public class NotExpression implements Expression
{
    
    private Expression negateMe;
    
    public NotExpression(Expression negateMe)
    {
        this.negateMe = negateMe;
    }
    
    @Override
    public String codegen()
    {
        return "!" + negateMe;
    }
    
    @Override
    public String toString()
    {
        return "!" + negateMe;
    }
    
}
