package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class ForEachThenExpression extends ForEachExpression
{
    private Expression thenStatements;
    
    public ForEachThenExpression(List<Expression> init, Expression collection, Expression doStatements, Expression thenStatements)
    {
        super(init, collection, doStatements);
        this.thenStatements = thenStatements;
    }
}
