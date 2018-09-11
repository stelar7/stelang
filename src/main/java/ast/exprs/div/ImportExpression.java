package ast.exprs.div;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

public class ImportExpression implements Expression
{
    private String classname;
    private String location;
    
    public ImportExpression(String classname, String location)
    {
        this.classname = classname;
        this.location = location;
    }
    
    public String getClassname()
    {
        return classname;
    }
    
    public String getLocation()
    {
        return location;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        return String.format("import %s from %s", classname, location);
    }
}
