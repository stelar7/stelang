package ast.exprs.basic;

import ast.exprs.Expression;

import java.util.List;

public class CallExpression implements Expression
{
    private String           caller;
    private List<Expression> arguments;
    
    public CallExpression(String caller, List<Expression> arguments)
    {
        this.caller = caller;
        this.arguments = arguments;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
