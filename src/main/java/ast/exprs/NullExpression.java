package ast.exprs;

public class NullExpression extends Expression
{
    @Override
    public String codegen()
    {
        return null;
    }
}
