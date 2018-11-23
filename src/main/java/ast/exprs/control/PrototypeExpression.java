package ast.exprs.control;

import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.LLVM.LLVMTypeRef;

import java.util.List;

import static org.bytedeco.javacpp.LLVM.*;

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
            refs[i] = UtilHander.getLLVMStruct(parameters.get(i).getType(), null);
        }
        return refs;
    }
    
    public LLVMTypeRef[] getParametersAsPointerTypeRefs()
    {
        LLVMTypeRef[] refs = new LLVMTypeRef[parameters.size()];
        for (int i = 0; i < parameters.size(); i++)
        {
            refs[i] = LLVMPointerType(UtilHander.getLLVMStruct(parameters.get(i).getType(), null), 0);
        }
        return refs;
    }
    
    @Override
    public String toString()
    {
        return "PrototypeExpression{" +
               "name='" + name + '\'' +
               ", parameters=" + parameters +
               ", returnType='" + returnType + '\'' +
               '}';
    }
}
