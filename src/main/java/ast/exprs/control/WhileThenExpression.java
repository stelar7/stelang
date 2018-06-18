package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class WhileThenExpression extends WhileExpression
{
    private List<Expression> thenStatements;
    
    public WhileThenExpression(Expression condition, List<Expression> doStatements, List<Expression> thenStatements)
    {
        super(condition, doStatements);
        this.thenStatements = thenStatements;
    }
    
    @Override
    public String codegen()
    {
        String sup = super.codegen();
        
        return sup;
    }
}
