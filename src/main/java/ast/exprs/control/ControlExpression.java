package ast.exprs.control;

import ast.exprs.Expression;

public abstract class ControlExpression extends Expression
{
    public abstract String codegen();
}
