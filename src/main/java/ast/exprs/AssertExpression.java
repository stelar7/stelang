package ast.exprs;

public class AssertExpression extends Expression
{
    private Expression condition;
    
    public AssertExpression(Expression condition)
    {
        this.condition = condition;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
