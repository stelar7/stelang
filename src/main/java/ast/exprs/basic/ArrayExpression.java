package ast.exprs.basic;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;

public class ArrayExpression implements Expression
{
    private List<Object> params;
    
    public ArrayExpression(List<Object> params)
    {
        this.params = params;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        
        return String.format("[%s x i32]", params.size());
    }
}
