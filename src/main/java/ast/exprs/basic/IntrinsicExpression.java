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
    
    @Override
    public Object codegen(Object... parent)
    {
        // module, function, builder, params
        LLVMBuilderRef            builderRef = (LLVMBuilderRef) parent[2];
        Map<String, LLVMValueRef> paramRefs  = (Map<String, LLVMValueRef>) parent[3];
        
        LLVMPrintModuleToFile((LLVMModuleRef) parent[0], "test", new BytePointer((Pointer) null));
        //LLVMVerifyModule((LLVMModuleRef) parent[0], LLVMAbortProcessAction, new BytePointer((Pointer) null));
        
        switch (function)
        {
            case "add64":
            {
                LLVMValueRef LHS = paramRefs.get(params.get(0));
                LLVMValueRef RHS = paramRefs.get(params.get(1));
                
                LLVMValueRef LHSPtr = LLVMBuildInBoundsGEP(builderRef, LHS, UtilHander.firstElement, 2, "LHSPtr");
                LLVMValueRef RHSPtr = LLVMBuildInBoundsGEP(builderRef, RHS, UtilHander.firstElement, 2, "RHSPtr");
                
                LLVMValueRef LHSVal = LLVMBuildLoad(builderRef, LHSPtr, "LHSval");
                LLVMValueRef RHSVal = LLVMBuildLoad(builderRef, RHSPtr, "RHSval");
                
                LLVMValueRef resultVal = LLVMBuildAdd(builderRef, LHSVal, RHSVal, "add64");
                
                LLVMValueRef valueRef = LLVMBuildAlloca(builderRef, UtilHander.getLLVMStruct("num", null), "result");
                LLVMValueRef elem     = LLVMBuildInBoundsGEP(builderRef, valueRef, UtilHander.firstElement, 2, "resultPtr");
                LLVMValueRef store    = LLVMBuildStore(builderRef, resultVal, elem);
                
                return valueRef;
            }
            
            case "set64":
            {
                LLVMValueRef LHS = paramRefs.get(params.get(0));
                LLVMValueRef RHS = paramRefs.get(params.get(1));
                
                LLVMValueRef LHSPtr = LLVMBuildInBoundsGEP(builderRef, LHS, UtilHander.firstElement, 2, "LHSPtr");
                LLVMValueRef RHSPtr = LLVMBuildInBoundsGEP(builderRef, RHS, UtilHander.firstElement, 2, "RHSPtr");
                
                LLVMValueRef LHSVal = LLVMBuildLoad(builderRef, LHSPtr, "LHSval");
                LLVMValueRef RHSVal = LLVMBuildLoad(builderRef, RHSPtr, "RHSval");
                
                LLVMBuildStore(builderRef, RHSVal, LHSPtr);
                return LHS;
            }
            
            case "cmpLS64":
            {
                LLVMValueRef LHS    = paramRefs.get(params.get(0));
                LLVMValueRef RHS    = paramRefs.get(params.get(1));
                
                LLVMValueRef LHSPtr = LLVMBuildInBoundsGEP(builderRef, LHS, UtilHander.firstElement, 2, "LHSPtr");
                LLVMValueRef RHSPtr = LLVMBuildInBoundsGEP(builderRef, RHS, UtilHander.firstElement, 2, "RHSPtr");
                
                LLVMValueRef LHSVal = LLVMBuildLoad(builderRef, LHSPtr, "LHSval");
                LLVMValueRef RHSVal = LLVMBuildLoad(builderRef, RHSPtr, "RHSval");
                
                LLVMValueRef resultVal = LLVMBuildICmp(builderRef, LLVMIntSLT, LHSVal, RHSVal, "cmpLS64");
    
                LLVMValueRef valueRef = LLVMBuildAlloca(builderRef, UtilHander.getLLVMStruct("bool", null), "result");
                LLVMValueRef elem     = LLVMBuildInBoundsGEP(builderRef, valueRef, UtilHander.firstElement, 2, "resultPtr");
                LLVMValueRef store    = LLVMBuildStore(builderRef, resultVal, elem);
                
                return valueRef;
            }
        }
        return null;
    }
}
