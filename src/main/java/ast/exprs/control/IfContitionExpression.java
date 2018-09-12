package ast.exprs.control;

import ast.exprs.Expression;
import ast.exprs.basic.ChainCompareExpression;
import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.LLVM.*;
import org.bytedeco.javacpp.PointerPointer;

import static org.bytedeco.javacpp.LLVM.*;

public class IfContitionExpression implements Expression
{
    private Expression cond;
    
    public IfContitionExpression(Expression parseExpression)
    {
        this.cond = parseExpression;
    }
    
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[2];
        
        return cond.codegen(obj);
    }
}
