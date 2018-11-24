package ast.exprs.basic;

import ast.exprs.Expression;
import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.LLVM.*;
import org.bytedeco.javacpp.PointerPointer;

import java.util.*;
import java.util.stream.Collectors;

import static org.bytedeco.javacpp.LLVM.*;

public class CallExpression implements Expression
{
    private String           methodName;
    private List<Expression> arguments;
    
    public CallExpression(String caller, List<Expression> arguments)
    {
        this.methodName = caller;
        this.arguments = arguments;
    }
    
    public String getMethodName()
    {
        return methodName;
    }
    
    public List<Expression> getArguments()
    {
        return arguments;
    }
    
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMModuleRef  module  = (LLVMModuleRef) obj[0];
        LLVMValueRef   parent  = (LLVMValueRef) obj[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[2];
        
        LLVMValueRef method = UtilHander.getLLVMMethod( "STL1<24self3num5other3num4bool");
        if (methodName.equals(UtilHander.externalCallName))
        {
            String methodName = ((TextExpression) arguments.get(0)).content;
            String returnType = ((TextExpression) arguments.get(1)).content;
            method = UtilHander.getLLVMMethod( methodName);
            if (method == null)
            {
                List<Expression> args = arguments.subList(2, arguments.size());
                LLVMTypeRef[]    refs = new LLVMTypeRef[args.size()];
                for (int i = 0; i < args.size(); i++)
                {
                    LLVMValueRef valRef = (LLVMValueRef) args.get(i).codegen(obj);
                    refs[i] = LLVMTypeOf(valRef);
                }
                PointerPointer rpoint    = new PointerPointer(refs);
                LLVMTypeRef    prototype = LLVMFunctionType(UtilHander.getLLVMStruct(returnType, null), rpoint, refs.length, 0);
                method = UtilHander.addLLVMMethod(methodName, LLVMAddFunction(module, methodName, prototype));
            }
        }
        
        List<LLVMValueRef> refs = new ArrayList<>();
        for (Expression arg : arguments)
        {
            LLVMValueRef ref = (LLVMValueRef) arg.codegen(obj);
            if (ref != null)
            {
                refs.add(ref);
            }
        }
        
        LLVMValueRef[] call_op_args = new LLVMValueRef[refs.size()];
        for (int i = 0; i < refs.size(); i++)
        {
            call_op_args[i] = refs.get(i);
        }
        
        String       args    = arguments.stream().map(Expression::toString).collect(Collectors.joining(","));
        LLVMValueRef call_op = LLVMBuildCall(builder, method, new PointerPointer(call_op_args), call_op_args.length, String.format("%s(%s)", methodName, args));
        
        return call_op;
    }
    
    @Override
    public String toString()
    {
        String args = arguments.stream().map(Expression::toString).collect(Collectors.joining(","));
        return String.format("%s(%s)", methodName, args);
    }
}
