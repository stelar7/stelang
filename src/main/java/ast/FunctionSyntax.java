package ast;

import ast.exprs.Expression;

public class FunctionSyntax
{
    private PrototypeSyntax prototype;
    private Expression      body;
    
    public FunctionSyntax(PrototypeSyntax prototype, Expression body)
    {
        this.prototype = prototype;
        this.body = body;
    }
}
