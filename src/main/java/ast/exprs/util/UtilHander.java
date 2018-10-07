package ast.exprs.util;

import ast.exprs.Expression;
import ast.exprs.clazz.ClassExpression;
import org.bytedeco.javacpp.LLVM.LLVMTypeRef;
import org.bytedeco.javacpp.PointerPointer;

import java.util.*;

import static org.bytedeco.javacpp.LLVM.*;

public class UtilHander
{
    private static Map<String, LLVMTypeRef>  classPointerTable    = new HashMap<>();
    private static Map<String, LLVMValueRef> classMethodTable     = new HashMap<>();
    private static Map<String, LLVMValueRef> variablePointerTable = new HashMap<>();
    private static Map<String, LLVMValueRef> globalPointerTable   = new HashMap<>();
    private static Map<String, String>       variableTypeTable    = new HashMap<>();
    public static  Map<String, LLVMValueRef> NULLS                = new HashMap<>();
    
    private static LLVMValueRef mainMethod;
    
    public static String mainMethodName   = "application_start";
    public static String externalCallName = "externCcall";
    
    public static LLVMValueRef getMainMethod()
    {
        return mainMethod;
    }
    
    public static void setMainMethod(LLVMValueRef mainMethod)
    {
        UtilHander.mainMethod = mainMethod;
    }
    
    public static void computeLLVMStructs()
    {
        {
            LLVMTypeRef    ref       = classPointerTable.computeIfAbsent("num", (key) -> LLVMStructCreateNamed(LLVMGetGlobalContext(), key));
            LLVMTypeRef[]  types_arr = {LLVMInt64Type()};
            PointerPointer types     = new PointerPointer(types_arr);
            LLVMStructSetBody(ref, types, 1, 0);
        }
        {
            LLVMTypeRef    ref       = classPointerTable.computeIfAbsent("null", (key) -> LLVMStructCreateNamed(LLVMGetGlobalContext(), key));
            LLVMTypeRef[]  types_arr = {LLVMVoidType()};
            PointerPointer types     = new PointerPointer(types_arr);
            LLVMStructSetBody(ref, types, 1, 0);
        }
        
        classPointerTable.forEach((key, value) -> NULLS.put(key, LLVMConstNull(value)));
    }
    
    public static LLVMTypeRef getLLVMStruct(String clazz, ClassExpression exp)
    {
        if (classPointerTable.get(clazz) != null)
        {
            return classPointerTable.get(clazz);
        }
        
        LLVMTypeRef ref = classPointerTable.computeIfAbsent(clazz, (key) -> LLVMStructCreateNamed(LLVMGetGlobalContext(), key));
        NULLS.put(clazz, LLVMConstNull(ref));
        
        if (exp != null)
        {
            LLVMTypeRef[] types = exp.getParameterTypes();
            if (types.length != 0)
            {
                LLVMStructSetBody(ref, types[0], types.length, 0);
            }
        }
        return ref;
    }
    
    
    public static LLVMValueRef generateVariable(LLVMBuilderRef builder, String identifier, String type)
    {
        variableTypeTable.put(identifier, type);
        LLVMValueRef value = LLVMBuildAlloca(builder, getLLVMStruct(type, null), identifier);
        variablePointerTable.put(identifier, value);
        return value;
    }
    
    public static String lookupVariableType(String name)
    {
        return variableTypeTable.get(name);
    }
    
    public static LLVMValueRef getLLVMMethod(Expression left, String methodName)
    {
        return classMethodTable.get(methodName);
    }
    
    public static LLVMValueRef addLLVMMethod(String functionName, LLVMValueRef function)
    {
        return classMethodTable.computeIfAbsent(functionName, (key) -> function);
    }
    
    public static LLVMValueRef getVariable(String name)
    {
        return variablePointerTable.get(name);
    }
    
    public static LLVMValueRef generateGlobal(LLVMModuleRef module, String identifier, String type)
    {
        LLVMValueRef var = LLVMAddGlobal(module, getLLVMStruct(type, null), identifier);
        return globalPointerTable.computeIfAbsent(identifier, (key) -> var);
    }
    
    public static LLVMValueRef getGlobal(String name)
    {
        return globalPointerTable.get(name);
    }
}
