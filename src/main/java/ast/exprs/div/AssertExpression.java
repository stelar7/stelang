package ast.exprs.div;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

public class AssertExpression implements Expression
{
    private Expression condition;
    
    public AssertExpression(Expression condition)
    {
        this.condition = condition;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        return "assert " + condition.codegen();
    }
}
