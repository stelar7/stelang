package ast.exprs.basic;

import ast.exprs.Expression;
import ast.exprs.util.UtilHander;
import lexer.*;
import org.bytedeco.javacpp.LLVM.*;
import org.bytedeco.javacpp.PointerPointer;

import static org.bytedeco.javacpp.LLVM.*;

public class BinaryExpression implements Expression
{
    private Token      op;
    private Expression left;
    private Expression right;
    
    public BinaryExpression(Token op, Expression left, Expression right)
    {
        this.op = op;
        this.left = left == null ? new NullExpression() : left;
        this.right = right == null ? new NullExpression() : right;
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
    public Object codegen(Object... obj)
    {
    
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[2];
        
        LLVMValueRef leftCode  = (LLVMValueRef) left.codegen(obj);
        LLVMValueRef rightCode = (LLVMValueRef) right.codegen(obj);
        
        LLVMValueRef   method       = UtilHander.getLLVMMethod(left, op.getContent());
        LLVMValueRef[] call_op_args = {leftCode, rightCode};
        LLVMValueRef   call_op      = LLVMBuildCall(builder, method, new PointerPointer(call_op_args), 2, this.toString());
        
        return call_op;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s %s %s", left.toString(), op.getContent(), right.toString());
    }
    
    @Override
    public int getSortOrder()
    {
        return 2;
    }
}
