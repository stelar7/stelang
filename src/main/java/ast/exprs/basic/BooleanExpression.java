package ast.exprs.basic;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

public class BooleanExpression implements Expression
{
    
    private boolean result;
    
    public BooleanExpression(boolean result)
    {
        this.result = result;
    }
    
    public boolean isResult()
    {
        return result;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        return String.valueOf(result);
    }
}
