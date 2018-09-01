package ast.exprs.basic;

import ast.exprs.Expression;

public class ArrayAccessExpression implements Expression
{
    
    private Expression variable;
    private Expression index;
    
    public ArrayAccessExpression(Expression variable, Expression index)
    {
        this.variable = variable;
        this.index = index;
    }
    
    @Override
    public String codegen()
    {
        return variable + "[" + index + "]";
    }
}
