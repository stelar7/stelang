package ast.exprs.div;

import ast.exprs.Expression;

public class TernaryExpression implements Expression
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
        String value = condition.codegen();
        
        String ifCond    = "fcmp one double " + value;
        String thenVal   = trueExpression.codegen();
        String elseVal   = falseExpression.codegen();
        String returnVal = String.format("br i1 %s, label %s, label %s", ifCond, thenVal, elseVal);
        
        return returnVal;
    }
}
