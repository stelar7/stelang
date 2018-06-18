package ast.exprs.div;

import ast.exprs.Expression;

public class ReturnExpression implements Expression
{
    Expression value;
    
    public ReturnExpression(Expression value)
    {
        this.value = value;
    }
    
    public Expression getReturnValue()
    {
        return value;
    }
    
    @Override
    public String codegen()
    {
        return "return " + value.codegen();
    }
}
