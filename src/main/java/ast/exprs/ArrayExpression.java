package ast.exprs;

import java.util.List;

public class ArrayExpression extends Expression
{
    private List<Object> params;
    
    public ArrayExpression(List<Object> params)
    {
        this.params = params;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
