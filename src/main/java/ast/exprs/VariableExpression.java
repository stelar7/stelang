package ast.exprs;

public class VariableExpression extends Expression
{
    private String name;
    
    public VariableExpression(String name)
    {
        this.name = name;
    }
}
