package ast.exprs.basic;

import ast.exprs.Expression;
import ast.exprs.control.IfContitionExpression;
import org.bytedeco.javacpp.LLVM.*;
import org.bytedeco.javacpp.PointerPointer;

import static org.bytedeco.javacpp.LLVM.*;

public class NotExpression implements Expression
{
    private IfContitionExpression negateMe;
    
    public NotExpression(IfContitionExpression negateMe)
    {
        this.negateMe = negateMe;
    }
    
    @Override
    public String toString()
    {
        return "!" + negateMe;
    }
    
    public LLVMValueRef codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        LLVMValueRef ifStmt = LLVMBuildICmp(builder, LLVMIntEQ, (LLVMValueRef) negateMe.codegen(obj), LLVMConstInt(LLVMInt32Type(), 0, 0), "value == 0");
        
        LLVMBasicBlockRef ifTrue  = LLVMAppendBasicBlock(parent, "ifTrue");
        LLVMBasicBlockRef ifFalse = LLVMAppendBasicBlock(parent, "ifFalse");
        LLVMBasicBlockRef end     = LLVMAppendBasicBlock(parent, "end");
        
        LLVMPositionBuilderAtEnd(builder, ifTrue);
        LLVMValueRef res_true = LLVMConstInt(LLVMInt1Type(), 1, 0);
        LLVMBuildBr(builder, end);
        
        LLVMPositionBuilderAtEnd(builder, ifFalse);
        LLVMValueRef res_false = LLVMConstInt(LLVMInt1Type(), 0, 0);
        LLVMBuildBr(builder, end);
        
        LLVMBuildCondBr(builder, ifStmt, ifTrue, ifFalse);
        LLVMPositionBuilderAtEnd(builder, end);
        
        LLVMValueRef        result     = LLVMBuildPhi(builder, LLVMInt1Type(), "result");
        LLVMValueRef[]      phi_vals   = {res_true, res_false};
        LLVMBasicBlockRef[] phi_blocks = {ifTrue, ifFalse};
        
        LLVMAddIncoming(result, new PointerPointer(phi_vals), new PointerPointer(phi_blocks), 2);
        LLVMBuildRet(builder, result);
        
        return null;
    }
}
