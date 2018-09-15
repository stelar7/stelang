package ast.exprs;

import ast.exprs.control.ControlExpression;

import java.util.List;

public class MultiExpression extends ControlExpression
{
    private List<Expression> list;
    
    public MultiExpression(List<Expression> list)
    {
        this.list = list;
    }
    
    public List<Expression> getList()
    {
        return list;
    }
    
    @Override
    public Object codegen(Object... parent)
    {
        return null;
    }
}
