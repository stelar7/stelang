package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class FunctionExpression extends ControlExpression
{
    private String              visibility;
    private PrototypeExpression prototype;
    private Expression          body;
    
    public FunctionExpression(String visibility, PrototypeExpression prototype, Expression body)
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
