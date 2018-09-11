package ast.exprs.control;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

public class ContinueStatement implements Expression
{
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        return "continue";
    }
}
