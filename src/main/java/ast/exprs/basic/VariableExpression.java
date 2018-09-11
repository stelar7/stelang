package ast.exprs.basic;

import ast.exprs.Expression;
import ast.exprs.clazz.ClassExpression;
import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.LLVM.*;

import java.util.Map;

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
        
        if (obj.length >= 3 && obj[2] instanceof Map)
        {
            LLVMValueRef val = ((Map<String, LLVMValueRef>) obj[2]).get(name);
            if (val != null)
            {
                return val;
            }
        }
        
        LLVMValueRef local = UtilHander.getVariable(name);
        if (local != null)
        {
            return local;
        }
        
        return UtilHander.getGlobal(name);
    }
}
