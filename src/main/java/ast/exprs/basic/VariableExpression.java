package ast.exprs.basic;

import ast.exprs.Expression;
import ast.exprs.clazz.ClassExpression;
import org.bytedeco.javacpp.LLVM.*;

public class VariableExpression implements Expression
{
    private String name;
    
    public VariableExpression(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        return String.format("%%%s", name);
    }
}
