package ast.exprs;

import ast.exprs.control.PrototypeExpression;

import java.util.List;

public class FunctionExpression extends Expression
{
    private String              visibility;
    private PrototypeExpression prototype;
    private List<Expression>    body;
    
    public FunctionExpression(String visibility, PrototypeExpression prototype, List<Expression> body)
    {
        this.visibility = visibility;
        this.prototype = prototype;
        this.body = body;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
