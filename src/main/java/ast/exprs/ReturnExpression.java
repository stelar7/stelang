package ast.exprs;

public class ReturnExpression extends Expression
{
    Expression value;
    
    public ReturnExpression(Expression value)
    {
        this.value = value;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
