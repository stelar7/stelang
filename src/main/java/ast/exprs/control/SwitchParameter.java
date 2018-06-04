package ast.exprs.control;

import ast.exprs.Expression;

public class SwitchParameter
{
    private int        caseIndex;
    private Expression condition;
    private Expression expression;
    
    public SwitchParameter(int caseIndex, Expression condition, Expression expression)
    {
        this.caseIndex = caseIndex;
        this.condition = condition;
        this.expression = expression;
    }
}
