package ast.exprs;

public class NumberExpression extends Expression
{
    private double val;
    
    public NumberExpression(double val)
    {
        this.val = val;
    }
}
