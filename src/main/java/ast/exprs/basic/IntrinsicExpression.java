package ast.exprs.basic;

import ast.exprs.Expression;
import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.*;

import java.util.*;

import static org.bytedeco.javacpp.LLVM.*;

public class IntrinsicExpression implements Expression
{
    
    private String       function;
    private List<String> params;
    
    public IntrinsicExpression(String function, List<String> params)
    {
        this.function = function;
        this.params = params;
    }
    
    private LLVMValueRef callMethod(LLVMBuilderRef builderRef, Map<String, LLVMValueRef> paramRefs, LLVMValueRef method)
    {
        String args = String.join(",", params);
        
        LLVMValueRef[] call_op_args = new LLVMValueRef[params.size()];
        for (int i = 0; i < call_op_args.length; i++)
        {
            call_op_args[i] = paramRefs.get(params.get(i));
        }
        
        LLVMValueRef call_op = LLVMBuildCall(builderRef, method, new PointerPointer(call_op_args), call_op_args.length, String.format("%s(%s)", function, args));
        return call_op;
    }
    
    @Override
    public Object codegen(Object... parent)
    {
        // module, function, builder, params
        LLVMBuilderRef            builderRef = (LLVMBuilderRef) parent[2];
        Map<String, LLVMValueRef> paramRefs  = (Map<String, LLVMValueRef>) parent[3];
        
        LLVMValueRef method = UtilHander.getIntrinsic(function);
        if (method != null)
        {
            return callMethod(builderRef, paramRefs, method);
        }
    
        System.err.printf("Call to invalid intrinsic function found! (%s)%n", function);
        
        return null;
    }
}
