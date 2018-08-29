package ast.exprs.basic;

import ast.exprs.Expression;
import lexer.*;

public class BinaryExpression implements Expression
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
    
    public Token getOp()
    {
        return op;
    }
    
    public Expression getLeft()
    {
        return left;
    }
    
    public Expression getRight()
    {
        return right;
    }
    
    @Override
    public String codegen()
    {
        String leftCode  = left.codegen();
        String rightCode = right.codegen();
        
        switch (op.getType())
        {
            case PLUS:
            {
                return "add i32 " + leftCode + ", " + rightCode;
            }
            
            case MINUS:
            {
                return "sub i32 " + leftCode + ", " + rightCode;
            }
            
            case ASTERISK:
            {
                return "mul i32 " + leftCode + ", " + rightCode;
            }
            
            case SLASH:
            {
                return "udiv i32 " + leftCode + ", " + rightCode;
            }
        }
        
        return String.format("%s %s %s", leftCode, op.getType().getTokenChars(), rightCode);
    }
    
    @Override
    public int getSortOrder()
    {
        return 2;
    }
}
