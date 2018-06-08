package ast.exprs;

import ast.exprs.control.ControlExpression;

import java.util.List;

public class BlockExpression extends ControlExpression
{
    private List<Expression> body;
    
    public BlockExpression(List<Expression> body)
    {
        this.body = body;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
