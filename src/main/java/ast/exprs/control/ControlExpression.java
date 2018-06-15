package ast.exprs.control;

import ast.exprs.Expression;

public abstract class ControlExpression implements Expression
{
    public abstract String codegen();
}
