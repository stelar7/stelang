package ast.exprs.basic;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

public class FloatExpression implements Expression
{
    private double val;
    
    public FloatExpression(double val)
    {
        this.val = val;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        return "double " + val;
    }
    
    @Override
    public String toString()
    {
        return String.valueOf(val);
    }
}
