package ast.exprs.basic;

import ast.exprs.Expression;
import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.LLVM.*;

public class CreateExpression implements Expression
{
    Expression value;
    
    public CreateExpression(Expression value)
    {
        this.value = value;
    }
    
    public Expression getValue()
    {
        return value;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[2];
        
        /*
        LLVMTypeRef    typeRef = UtilHander.getLLVMStruct("num", null);
        LLVMValueRef   valueRef     = LLVMBuildAlloca(builder, typeRef, "numberVal");
        PointerPointer valuePointer = new PointerPointer(new LLVMValueRef[]{LLVMConstInt(LLVMInt64Type(), 1, 0)});
        LLVMValueRef   elem         = LLVMBuildGEP(builder, valueRef, valuePointer, 1, "numberPtr");
        LLVMValueRef   store        = LLVMBuildStore(builder, LLVMConstInt(LLVMInt64Type(), val, 0), elem);
        */
        return UtilHander.NULL;
    }
}
