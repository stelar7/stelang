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
