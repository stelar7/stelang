package ast.exprs.basic;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

public class TextExpression implements Expression
{
    String content;
    
    public TextExpression(String content)
    {
        this.content = content;
    }
    
    @Override
    public String toString()
    {
        return content;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[2];
    
        return null;
    }
}
