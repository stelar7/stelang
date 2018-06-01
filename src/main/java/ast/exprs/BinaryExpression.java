package ast.exprs;

import lexer.Token;

public class BinaryExpression extends Expression
{
    private Token      op;
    private Expression left;
    private Expression right;
    
    public BinaryExpression(Token op, Expression left, Expression right)
    {
        this.op = op;
        this.left = left;
        this.right = right;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
