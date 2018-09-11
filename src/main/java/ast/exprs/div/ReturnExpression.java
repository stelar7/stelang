package ast.exprs.div;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

import static org.bytedeco.javacpp.LLVM.*;

public class ReturnExpression implements Expression
{
    Expression value;
    
    public ReturnExpression(Expression value)
    {
        this.value = value;
    }
    
    public Expression getReturnValue()
    {
        return value;
    }
    
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        LLVMValueRef valueRef = (LLVMValueRef) value.codegen(obj);
        LLVMBuildRet(builder, valueRef);
        return null;
    }
}
