package ast.exprs.basic;


import ast.exprs.Expression;
import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.LLVM.*;

public class VariableDefinitionExpression implements Expression
{
    private String identifier;
    private String type;
    
    public VariableDefinitionExpression(String identifier, String visibility)
    {
        this.identifier = identifier;
        this.type = visibility;
    }
    
    public String getIdentifier()
    {
        return identifier;
    }
    
    public String getType()
    {
        return type;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        if (obj[0] instanceof LLVMValueRef)
        {
            LLVMValueRef   parent  = (LLVMValueRef) obj[0];
            LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
            return UtilHander.generateVariable(builder, identifier, type);
        }
        
        LLVMModuleRef module = (LLVMModuleRef) obj[0];
        return UtilHander.generateGlobal(module, identifier, type);
    }
    
    @Override
    public int getSortOrder()
    {
        return 1;
    }
}
