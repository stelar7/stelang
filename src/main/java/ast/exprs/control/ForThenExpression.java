package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class ForThenExpression extends ForExpression
{
    private Expression thenStatements;
    
    public ForThenExpression(List<Expression> init, List<Expression> condition, List<Expression> increment, Expression doStatements, Expression thenStatements)
    {
        super(init, condition, increment, doStatements);
        this.thenStatements = thenStatements;
    }
}
