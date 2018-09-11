package ast.exprs.basic;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

public class ArrayAccessExpression implements Expression
{
    
    private Expression variable;
    private Expression index;
    
    public ArrayAccessExpression(Expression variable, Expression index)
    {
        this.variable = variable;
        this.index = index;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        return variable + "[" + index + "]";
    }
}
