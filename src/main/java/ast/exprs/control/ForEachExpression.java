package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class ForEachExpression extends ControlExpression
{
    private List<Expression> init;
    private Expression       collection;
    private Expression       doStatements;
    
    public ForEachExpression(List<Expression> init, Expression collection, Expression doStatements)
    {
        this.init = init;
        this.collection = collection;
        this.doStatements = doStatements;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
