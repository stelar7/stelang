package ast.exprs.control;

import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.LLVM.LLVMTypeRef;

import java.util.List;

public class PrototypeExpression
{
    private String                   name;
    private List<PrototypeParameter> parameters;
    private String                   returnType;
    
    public PrototypeExpression(String name, List<PrototypeParameter> parameters, String returnType)
    {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
    }
    
    public String getName()
    {
        return name;
    }
    
    public List<PrototypeParameter> getParameters()
    {
        return parameters;
    }
    
    public String getReturnType()
    {
        return returnType;
    }
    
    public LLVMTypeRef[] getParametersAsTypeRefs()
    {
        LLVMTypeRef[] refs = new LLVMTypeRef[parameters.size()];
        for (int i = 0; i < parameters.size(); i++)
        {
            refs[i] = UtilHander.getLLVMType(parameters.get(i));
        }
        return refs;
    }
}
