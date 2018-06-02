package ast.exprs;

public class BooleanExpression extends Expression
{
    
    private boolean result;
    
    public BooleanExpression(boolean result)
    {
        this.result = result;
    }
    
    public boolean isResult()
    {
        return result;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
