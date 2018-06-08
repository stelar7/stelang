package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class SwitchParameter
{
    private int        caseIndex;
    private Expression condition;
    private Expression expressions;
    
    public SwitchParameter(int caseIndex, Expression condition, Expression expressions)
    {
        this.caseIndex = caseIndex;
        this.condition = condition;
        this.expressions = expressions;
    }
}
