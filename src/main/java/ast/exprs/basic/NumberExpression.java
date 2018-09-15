package ast.exprs.basic;

import ast.exprs.Expression;
import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.LLVM.*;
import org.bytedeco.javacpp.PointerPointer;

import static org.bytedeco.javacpp.LLVM.*;

public class NumberExpression implements Expression
{
    private long val;
    
    public NumberExpression(long val)
    {
        this.val = val;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMBuilderRef builder      = (LLVMBuilderRef) obj[2];
        LLVMTypeRef    typeRef      = UtilHander.getLLVMStruct("num", null);
        LLVMValueRef   valueRef     = LLVMBuildAlloca(builder, typeRef, "numberVal");
        PointerPointer valuePointer = new PointerPointer(new LLVMValueRef[]{LLVMConstInt(LLVMInt64Type(), 1, 0)});
        LLVMValueRef   elem         = LLVMBuildGEP(builder, valueRef, valuePointer, 1, "numberPtr");
        LLVMValueRef   store        = LLVMBuildStore(builder, LLVMConstInt(LLVMInt64Type(), val, 0), elem);
        
        return store;
    }
    
    @Override
    public String toString()
    {
        return String.valueOf(val);
    }
}
