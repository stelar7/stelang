package ast.exprs;

import java.util.List;

public class CallExpression extends Expression
{
    private String           caller;
    private List<Expression> arguments;
    
    public CallExpression(String caller, List<Expression> arguments)
    {
        this.caller = caller;
        this.arguments = arguments;
    }
}
