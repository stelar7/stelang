package ast.exprs.div;

import ast.exprs.Expression;

public class ReturnExpression extends Expression
{
    Expression value;
    
    public ReturnExpression(Expression value)
    {
        this.value = value;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
