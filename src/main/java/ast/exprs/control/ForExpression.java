package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class ForExpression extends ControlExpression
{
    private Expression       init;
    private Expression       condition;
    private Expression       increment;
    private List<Expression> doStatements;
    
    public ForExpression(Expression init, Expression condition, Expression increment, List<Expression> doStatements)
    {
        this.init = init;
        this.condition = condition;
        this.increment = increment;
        this.doStatements = doStatements;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
