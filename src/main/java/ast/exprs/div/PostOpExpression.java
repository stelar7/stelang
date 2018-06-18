package ast.exprs.div;

import ast.exprs.Expression;
import lexer.*;

public class PostOpExpression implements Expression
{
    private Expression variable;
    private TokenType  operator;
    
    public PostOpExpression(Expression variable, TokenType currentToken)
    {
        this.variable = variable;
        this.operator = currentToken.toFirstToken();
    }
    
    @Override
    public String codegen()
    {
        String var = variable.codegen();
        
        return String.format("%s = %s %s 1", var, var, operator);
    }
}
