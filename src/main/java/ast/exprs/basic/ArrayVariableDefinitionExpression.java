package ast.exprs.basic;


import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.LLVMBuilderRef;

public class ArrayVariableDefinitionExpression implements Expression
{
    private String identifier;
    private String type;
    
    public ArrayVariableDefinitionExpression(String identifier, String visibility)
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
        
        LLVMBuilderRef builderRef;
        if (obj[1] instanceof LLVMBuilderRef)
        {
            builderRef = (LLVMBuilderRef) obj[1];
        } else
        {
            builderRef = (LLVMBuilderRef) obj[2];
        }
        /*
        LLVMValueRef valueRef = LLVMBuildAlloca(builderRef, UtilHander.getLLVMStruct(type, null), "created");
        return valueRef;
        */
        return null;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s:%s", type, identifier);
    }
    
    @Override
    public int getSortOrder()
    {
        return 1;
    }
}
