package ast.exprs.basic;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

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
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
    
        return LLVMConstInt(LLVMInt64Type(), val, 0);
    }
    
    @Override
    public String toString()
    {
        return String.valueOf(val);
    }
}
