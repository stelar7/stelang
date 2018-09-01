package ast.exprs.control;

import ast.exprs.Expression;

public class BreakStatement implements Expression
{
    @Override
    public String codegen()
    {
        return "break";
    }
}
