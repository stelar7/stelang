package ast.exprs.div;

import ast.exprs.Expression;

public class AssertExpression extends Expression
{
    private Expression condition;
    
    public AssertExpression(Expression condition)
    {
        this.condition = condition;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
