package ast;

import ast.exprs.Expression;

import java.util.List;

public class FunctionSyntax extends Syntax
{
    private String           visibility;
    private PrototypeSyntax  prototype;
    private List<Expression> body;
    
    public FunctionSyntax(String visibility, PrototypeSyntax prototype, List<Expression> body)
    {
        this.visibility = visibility;
        this.prototype = prototype;
        this.body = body;
    }
}
