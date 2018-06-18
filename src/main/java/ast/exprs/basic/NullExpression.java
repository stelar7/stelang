package ast.exprs.basic;

import ast.exprs.Expression;

public class NullExpression implements Expression
{
    @Override
    public String codegen()
    {
        return "void";
    }
}
