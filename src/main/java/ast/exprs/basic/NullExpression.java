package ast.exprs.basic;

import ast.exprs.Expression;

public class NullExpression extends Expression
{
    @Override
    public String codegen()
    {
        return null;
    }
}
