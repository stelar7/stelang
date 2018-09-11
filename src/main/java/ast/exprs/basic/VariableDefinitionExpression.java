package ast.exprs.basic;


import ast.exprs.Expression;
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
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        return String.format("%%%s", identifier);
    }
    
    @Override
    public int getSortOrder()
    {
        return 1;
    }
}
