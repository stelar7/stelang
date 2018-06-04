package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class DoWhileExpression extends ControlExpression
{
    private Expression       condition;
    private List<Expression> doStatement;
    
    public DoWhileExpression(Expression condition, List<Expression> doStatement)
    {
        this.condition = condition;
        this.doStatement = doStatement;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
