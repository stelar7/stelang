package ast.exprs.basic;

import ast.exprs.Expression;

import java.util.List;

public class CallExpression implements Expression
{
    private String           metodName;
    private List<Expression> arguments;
    
    public CallExpression(String caller, List<Expression> arguments)
    {
        this.metodName = caller;
        this.arguments = arguments;
    }
    
    public String getMetodName()
    {
        return metodName;
    }
    
    public List<Expression> getArguments()
    {
        return arguments;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
