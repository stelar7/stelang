package ast.exprs.control;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

public class IfContitionExpression implements Expression
{
    private Expression cond;
    
    public IfContitionExpression(Expression parseExpression)
    {
        this.cond = parseExpression;
    }
    
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        
        return null;
    }
}
