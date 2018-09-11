package ast.exprs.clazz;

import ast.exprs.Expression;
import ast.exprs.control.*;
import ast.exprs.util.UtilHander;

import static org.bytedeco.javacpp.LLVM.*;

import java.util.*;

public class FunctionExpression extends ControlExpression
{
    private String              visibility;
    private PrototypeExpression prototype;
    private BlockExpression     body;
    
    public FunctionExpression(String visibility, PrototypeExpression prototype, BlockExpression body)
    {
        this.visibility = visibility;
        this.prototype = prototype;
        this.body = body;
    }
    
    public String getVisibility()
    {
        return visibility;
    }
    
    public PrototypeExpression getPrototype()
    {
        return prototype;
    }
    
    public void setPrototype(PrototypeExpression prototype)
    {
        this.prototype = prototype;
    }
    
    public List<Expression> getBody()
    {
        return body.getBody();
    }
    
    public BlockExpression getBlock()
    {
        return body;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMModuleRef  parent  = (LLVMModuleRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        LLVMTypeRef    clazz   = (LLVMTypeRef) obj[2];
        
        String functionName = prototype.getName();
        
        LLVMTypeRef   returnType = UtilHander.getLLVMStruct(prototype.getReturnType(), null);
        LLVMTypeRef[] arguments  = prototype.getParametersAsTypeRefs();
        LLVMTypeRef   functionPrototype;
        if (arguments.length != 0)
        {
            functionPrototype = LLVMFunctionType(returnType, arguments[0], arguments.length, 0);
        } else
        {
            functionPrototype = LLVMFunctionType(returnType, LLVMVoidType(), 1, 0);
        }
        
        LLVMValueRef function = UtilHander.addLLVMMethod(functionName, LLVMAddFunction(parent, functionName, functionPrototype));
        LLVMSetFunctionCallConv(function, LLVMCCallConv);
        
        Map<String, LLVMValueRef> params = new HashMap<>();
        for (int i = 0; i < arguments.length; i++)
        {
            LLVMValueRef ref = LLVMGetParam(function, i);
            params.put(prototype.getParameters().get(i).getName(), ref);
        }
        
        body.codegen(function, builder, params);
        
        if (functionName.equals(UtilHander.mainMethodName))
        {
            UtilHander.setMainMethod(function);
        }
        
        return null;
    }
}
