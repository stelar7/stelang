package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;
import java.util.stream.Collectors;

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
        String value = condition.codegen();
        
        String ifCond    = "fcmp one double " + value;
        String thenVal   = trueExpressions.stream().map(Expression::codegen).collect(Collectors.joining(";"));
        String elseVal   = falseExpressions.stream().map(Expression::codegen).collect(Collectors.joining(";"));
        String returnVal = String.format("br i1 %s, label %s, label %s", ifCond, thenVal, elseVal);
        
        return returnVal;
    }
}
