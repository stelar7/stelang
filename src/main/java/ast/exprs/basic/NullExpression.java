package ast.exprs.basic;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

import static org.bytedeco.javacpp.LLVM.*;

public class NullExpression implements Expression
{
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[2];
        
        return LLVMConstInt(LLVMInt1Type(), 0, 0);
    }
    
    @Override
    public String toString()
    {
        return "null(0)";
    }
}
