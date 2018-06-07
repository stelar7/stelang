package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class ForEachThenExpression extends ForEachExpression
{
    private List<Expression> thenStatements;
    
    public ForEachThenExpression(List<Expression> init, Expression collection, List<Expression> doStatements, List<Expression> thenStatements)
    {
        super(init, collection, doStatements);
        this.thenStatements = thenStatements;
    }
}
