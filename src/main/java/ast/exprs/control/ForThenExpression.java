package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class ForThenExpression extends ForExpression
{
    private List<Expression> thenStatements;
    
    public ForThenExpression(Expression init, Expression condition, Expression increment, List<Expression> doStatements, List<Expression> thenStatements)
    {
        super(init, condition, increment, doStatements);
        this.thenStatements = thenStatements;
    }
}
