package ast.exprs.basic;

import ast.exprs.Expression;
import ast.exprs.clazz.ClassExpression;
import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.LLVM.*;

import java.util.Map;

import static org.bytedeco.javacpp.LLVM.*;

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
    public String toString()
    {
        return name;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[2];
        LLVMValueRef   ref     = null;
        
        if (name.equals("null"))
        {
            return UtilHander.NULL;
        }
        
        if (obj.length >= 4 && obj[3] instanceof Map)
        {
            ref = ((Map<String, LLVMValueRef>) obj[3]).get(name);
            if (ref != null)
            {
                return ref;
            }
        }
        
        ref = UtilHander.getVariable(name);
        if (ref != null)
        {
            return ref;
        }
        
        ref = UtilHander.getGlobal(name);
        if (ref != null)
        {
            return LLVMBuildLoad(builder, ref, name);
        }
        
        return null;
    }
}
