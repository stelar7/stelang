package ast.exprs.div;

import ast.exprs.Expression;
import ast.exprs.control.*;

import java.util.List;

public class TernaryExpression implements Expression
{
    private IfExpression expr;
    
    public TernaryExpression(Expression condition, Expression trueExpression, Expression falseExpression)
    {
        this.expr = new IfExpression((IfContitionExpression) condition, List.of(trueExpression), List.of(falseExpression));
    }
    
    
    @Override
    public Object codegen(Object... obj)
    {
        return expr.codegen(obj);
    }
}
