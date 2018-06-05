package ast.exprs.div;

import ast.exprs.Expression;

import java.util.List;

public class TernaryExpression extends Expression
{
    private Expression condition;
    private Expression trueExpression;
    private Expression falseExpression;
    
    public TernaryExpression(Expression condition, Expression trueExpression, Expression falseExpression)
    {
        this.condition = condition;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }
    
    
    @Override
    public String codegen()
    {
        return null;
    }
}
