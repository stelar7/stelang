package ast.exprs;

import java.util.List;

public class ArrayExpression extends Expression
{
    private List<Long> params;
    
    public ArrayExpression(List<Long> params)
    {
        this.params = params;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
