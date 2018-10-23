package ast.exprs.basic;

import ast.exprs.Expression;
import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.LLVM.*;

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
    
    @Override
    public Object codegen(Object... parent)
    {
        // module, function, builder, params
        LLVMBuilderRef            builderRef = (LLVMBuilderRef) parent[2];
        Map<String, LLVMValueRef> paramRefs  = (Map<String, LLVMValueRef>) parent[3];
        LLVMValueRef              LHS        = paramRefs.get(params.get(0));
        
        LLVMPrintModuleToFile((LLVMModuleRef) parent[0], "test", new BytePointer((Pointer) null));
        
        switch (function)
        {
            case "add64":
            {
                LLVMValueRef RHS = paramRefs.get(params.get(1));
                return LLVM.LLVMBuildAdd(builderRef, LHS, RHS, "add64");
            }
            
            case "set64":
            {
                LLVMValueRef RHS = paramRefs.get(params.get(1));
                LLVM.LLVMBuildStore(builderRef, RHS, LHS);
                return RHS;
            }
            
            case "cmpLS64":
            {
                LLVMValueRef RHS     = paramRefs.get(params.get(1));
                LLVMTypeRef  numType = UtilHander.getLLVMStruct("num", null);
                
                PointerPointer valuePointer = new PointerPointer(new LLVMValueRef[]{LLVMConstInt(LLVMInt32Type(), 0, 0), LLVMConstInt(LLVMInt32Type(), 0, 0)});
                LLVMValueRef   LHSPtr       = LLVMBuildInBoundsGEP(builderRef, LHS, valuePointer, 2, "LHSPtr");
                LLVMValueRef   LHSVal       = LLVM.LLVMBuildLoad(builderRef, LHSPtr, "loadLHS");
                
                LLVMValueRef RHSPtr = LLVMBuildInBoundsGEP(builderRef, RHS, valuePointer, 2, "RHSPtr");
                LLVMValueRef RHSVal = LLVM.LLVMBuildLoad(builderRef, RHSPtr, "loadRHS");
                
                return LLVM.LLVMBuildICmp(builderRef, LLVM.LLVMIntSLT, LHSVal, RHSVal, "cmpLS64");
            }
        }
        return null;
    }
}
