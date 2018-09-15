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
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[2];
        
        LLVMValueRef valueRef = (LLVMValueRef) value.codegen(obj);
        LLVMBuildRet(builder, valueRef);
        return valueRef;
    }
}
