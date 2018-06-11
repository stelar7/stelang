package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class BlockExpression extends ControlExpression
{
    private List<Expression> body;
    
    public BlockExpression(List<Expression> body)
    {
        this.body = body;
    }
    
    public List<Expression> getBody()
    {
        return body;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
