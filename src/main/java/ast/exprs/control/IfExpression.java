package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class IfExpression extends ControlExpression
{
    private Expression       condition;
    private List<Expression> trueExpressions;
    private List<Expression> falseExpressions;
    
    public IfExpression(Expression condition, List<Expression> trueExpressions, List<Expression> falseExpressions)
    {
        this.condition = condition;
        this.trueExpressions = trueExpressions;
        this.falseExpressions = falseExpressions;
    }
    
    
    @Override
    public String codegen()
    {
        return null;
    }
}
