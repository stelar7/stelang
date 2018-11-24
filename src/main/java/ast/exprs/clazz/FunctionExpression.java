package ast.exprs.clazz;

import ast.exprs.Expression;
import ast.exprs.control.*;
import ast.exprs.div.ReturnExpression;
import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.*;

import static org.bytedeco.javacpp.LLVM.*;

import java.util.*;
import java.util.function.Supplier;

public class FunctionExpression extends ControlExpression
{
    public static List<Supplier<Optional<Void>>> bodies = new ArrayList<>();
    
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
        
        String mangledName = prototype.getMangledName();
        UtilHander.getFunctions().computeIfAbsent(mangledName, (mn) -> {
            
            LLVMTypeRef    returnType        = LLVMPointerType(UtilHander.getLLVMStruct(prototype.getReturnType(), null), 0);
            LLVMTypeRef[]  arguments         = prototype.getParametersAsPointerTypeRefs();
            PointerPointer args              = new PointerPointer(arguments);
            LLVMTypeRef    functionPrototype = LLVMFunctionType(returnType, args, arguments.length, 0);
            LLVMValueRef   function          = UtilHander.addLLVMMethod(mangledName, LLVMAddFunction(parent, mangledName, functionPrototype));
            LLVMSetFunctionCallConv(function, LLVMCCallConv);
            
            Map<String, LLVMValueRef> params = new HashMap<>();
            for (int i = 0; i < arguments.length; i++)
            {
                LLVMValueRef ref = LLVMGetParam(function, i);
                params.put(prototype.getParameters().get(i).getName(), ref);
            }
            
            LLVMBasicBlockRef entry = LLVMAppendBasicBlock(function, prototype.getName());
            LLVMPositionBuilderAtEnd(builder, entry);
            
            bodies.add(() -> {
                LLVMPositionBuilderAtEnd(builder, entry);
                body.codegen(parent, function, builder, params);
                return Optional.empty();
            });
            
            if (prototype.getName().equals(UtilHander.mainMethodName))
            {
                UtilHander.setMainMethod(function);
            }
            
            return function;
        });
        
        return null;
    }
    
    @Override
    public String toString()
    {
        return "FunctionExpression{" +
               "visibility='" + visibility + '\'' +
               ", prototype=" + prototype +
               ", body=" + body +
               '}';
    }
}
