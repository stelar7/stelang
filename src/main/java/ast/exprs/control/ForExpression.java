package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class ForExpression extends ControlExpression
{
    private List<Expression>       init;
    private List<Expression>       condition;
    private List<Expression>       increment;
    private List<Expression> doStatements;
    
    public ForExpression(List<Expression> init, List<Expression> condition, List<Expression> increment, List<Expression> doStatements)
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
