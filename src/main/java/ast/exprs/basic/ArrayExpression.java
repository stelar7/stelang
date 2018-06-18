package ast.exprs.basic;

import ast.exprs.Expression;

import java.util.List;

public class ArrayExpression implements Expression
{
    private List<Object> params;
    
    public ArrayExpression(List<Object> params)
    {
        this.params = params;
    }
    
    @Override
    public String codegen()
    {
        return String.format("[%s x i32]", params.size());
    }
}
