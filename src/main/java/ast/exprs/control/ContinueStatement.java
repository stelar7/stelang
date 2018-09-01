package ast.exprs.control;

import ast.exprs.Expression;

public class ContinueStatement implements Expression
{
    @Override
    public String codegen()
    {
        return "continue";
    }
}
