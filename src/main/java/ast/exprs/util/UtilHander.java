package ast.exprs.util;

import ast.exprs.control.PrototypeParameter;
import org.bytedeco.javacpp.LLVM.LLVMTypeRef;

import java.util.*;

import static org.bytedeco.javacpp.LLVM.*;

public class UtilHander
{
    private static Map<String, LLVMTypeRef> classPointerTable = new HashMap<>();
    private static LLVMValueRef             mainMethod;
    public static  String                   mainMethodName    = "application_start";
    
    public static LLVMValueRef getMainMethod()
    {
        return mainMethod;
    }
    
    public static void setMainMethod(LLVMValueRef mainMethod)
    {
        UtilHander.mainMethod = mainMethod;
    }
    
    public static LLVMTypeRef getLLVMStruct(String clazz)
    {
        return classPointerTable.computeIfAbsent(clazz, (key) -> generateRef(clazz));
    }
    
    private static LLVMTypeRef generateRef(String clazz)
    {
        LLVMTypeRef ref = LLVMStructCreateNamed(LLVMGetGlobalContext(), clazz);
        
        // todo set the content of the struct correctly...
        // LLVMStructSetBody(ref, ???, count, 0);
        
        return ref;
    }
    
    public static LLVMTypeRef getLLVMType(PrototypeParameter param)
    {
        return getLLVMStruct(param.getType());
    }
}
