package ast.exprs.clazz;

import ast.exprs.Expression;
import ast.exprs.basic.NullExpression;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;

public class EnumMemberExpression implements Expression
{
    private String           name;
    private List<Expression> params;
    
    public EnumMemberExpression(String name, List<Expression> params)
    {
        this.name = name;
        this.params = params;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        
        return String.format("%s(%s),\n", name, params);
    }
    
}
