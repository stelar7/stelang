package ast.exprs.util;

import ast.exprs.clazz.ClassExpression;
import org.bytedeco.javacpp.LLVM.*;
import org.bytedeco.javacpp.PointerPointer;

import java.util.*;

import static org.bytedeco.javacpp.LLVM.*;

public class UtilHander
{
    private static Map<String, LLVMValueRef> intrinsicFunctions = new HashMap<>();
    private static Map<String, LLVMValueRef> functionsTable     = new HashMap<>();
    
    private static Map<String, LLVMTypeRef>  classPointerTable    = new HashMap<>();
    private static Map<String, LLVMValueRef> classMethodTable     = new HashMap<>();
    private static Map<String, LLVMValueRef> variablePointerTable = new HashMap<>();
    private static Map<String, LLVMValueRef> globalPointerTable   = new HashMap<>();
    private static Map<String, String>       variableTypeTable    = new HashMap<>();
    public static  Map<String, LLVMValueRef> NULLS                = new HashMap<>();
    
    private static LLVMValueRef   mainMethod;
    public static  PointerPointer firstElement = new PointerPointer(new LLVMValueRef[]{LLVMConstInt(LLVMInt32Type(), 0, 0), LLVMConstInt(LLVMInt32Type(), 0, 0)});
    
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
    
    private static void generateLLVMStruct(String name, LLVMTypeRef[] elems)
    {
        LLVMTypeRef    ref   = classPointerTable.computeIfAbsent(name, (key) -> LLVMStructCreateNamed(LLVMGetGlobalContext(), key));
        PointerPointer types = new PointerPointer(elems);
        LLVMStructSetBody(ref, types, elems.length, 0);
    }
    
    
    public static void computeLLVMStructs()
    {
        generateLLVMStruct("i1", new LLVMTypeRef[]{LLVMInt8Type()});
        generateLLVMStruct("i8", new LLVMTypeRef[]{LLVMInt8Type()});
        generateLLVMStruct("i16", new LLVMTypeRef[]{LLVMInt16Type()});
        generateLLVMStruct("i32", new LLVMTypeRef[]{LLVMInt32Type()});
        generateLLVMStruct("i64", new LLVMTypeRef[]{LLVMInt64Type()});
        generateLLVMStruct("i128", new LLVMTypeRef[]{LLVMInt128Type()});
        
        generateLLVMStruct("f16", new LLVMTypeRef[]{LLVMHalfType()});
        generateLLVMStruct("f32", new LLVMTypeRef[]{LLVMFloatType()});
        generateLLVMStruct("f64", new LLVMTypeRef[]{LLVMDoubleType()});
        generateLLVMStruct("f80", new LLVMTypeRef[]{LLVMX86FP80Type()});
        generateLLVMStruct("f128", new LLVMTypeRef[]{LLVMFP128Type()});
        
        generateLLVMStruct("object", new LLVMTypeRef[]{});
        generateLLVMStruct("bool", new LLVMTypeRef[]{LLVMInt1Type()});
        generateLLVMStruct("void", new LLVMTypeRef[]{LLVMVoidType()});
        
        classPointerTable.forEach((key, value) -> NULLS.put(key, LLVMConstNull(value)));
    }
    
    public static void generateCBindings(LLVMModuleRef module, LLVMBuilderRef builder)
    {
        LLVMTypeRef    returnType        = LLVMInt32Type();
        PointerPointer args              = new PointerPointer(LLVMPointerType(LLVMInt8Type(), 0));
        LLVMTypeRef    functionPrototype = LLVMFunctionType(returnType, args, 1, 0);
        LLVMValueRef   method            = UtilHander.addLLVMMethod("puts", LLVMAddFunction(module, "puts", functionPrototype));
        LLVMSetFunctionCallConv(method, LLVMCCallConv);
    }
    
    private static LLVMValueRef generateFunctionHeader(LLVMModuleRef module, LLVMBuilderRef builderRef, String name, String ret, LLVMTypeRef[] arguments)
    {
        LLVMTypeRef    returnType        = LLVMPointerType(getLLVMStruct(ret, null), 0);
        PointerPointer args              = new PointerPointer(arguments);
        LLVMTypeRef    functionPrototype = LLVMFunctionType(returnType, args, arguments.length, 0);
        LLVMValueRef   method            = UtilHander.addLLVMMethod(name, LLVMAddFunction(module, name, functionPrototype));
        LLVMSetFunctionCallConv(method, LLVMCCallConv);
        intrinsicFunctions.putIfAbsent(name, method);
        
        LLVMBasicBlockRef entry = LLVMAppendBasicBlock(method, name);
        LLVMPositionBuilderAtEnd(builderRef, entry);
        
        return method;
    }
    
    
    public static void generateIntrinsicFunctions(LLVMModuleRef moduleRef, LLVMBuilderRef builderRef)
    {
        LLVMTypeRef   num     = UtilHander.getLLVMStruct("num", null);
        LLVMTypeRef   text    = UtilHander.getLLVMStruct("text", null);
        LLVMTypeRef[] intint  = new LLVMTypeRef[]{LLVMPointerType(num, 0), LLVMPointerType(num, 0)};
        LLVMTypeRef[] textRef = new LLVMTypeRef[]{LLVMPointerType(text, 0)};
        // print
        /*
        {
            PointerPointer bytes  = buildPointerPointer(0, 0);
            PointerPointer length = buildPointerPointer(0, 1);
            
            LLVMValueRef method   = generateFunctionHeader(moduleRef, builderRef, "print", "void", textRef);
            LLVMValueRef input    = LLVMGetParam(method, 0);
            LLVMValueRef inputPtr = LLVMBuildInBoundsGEP(builderRef, input, bytes, 2, "bytesGEP");
            LLVMBuildCall(builderRef, UtilHander.getLLVMMethod("puts"), new PointerPointer(inputPtr), 1, "puts");
            LLVMBuildRet(builderRef, LLVMConstInt(LLVMInt32Type(), 0, 0));
        }
         */
        /*
        // add64
        {
            LLVMValueRef method = generateFunctionHeader(moduleRef, builderRef, "add64", "num", intint);
            
            LLVMValueRef LHS = LLVMGetParam(method, 0);
            LLVMValueRef RHS = LLVMGetParam(method, 1);
            
            LLVMValueRef LHSPtr = LLVMBuildInBoundsGEP(builderRef, LHS, UtilHander.firstElement, 2, "LHSPtr");
            LLVMValueRef RHSPtr = LLVMBuildInBoundsGEP(builderRef, RHS, UtilHander.firstElement, 2, "RHSPtr");
            
            LLVMValueRef LHSVal = LLVMBuildLoad(builderRef, LHSPtr, "LHSval");
            LLVMValueRef RHSVal = LLVMBuildLoad(builderRef, RHSPtr, "RHSval");
            
            LLVMValueRef resultVal = LLVMBuildAdd(builderRef, LHSVal, RHSVal, "add64");
            
            LLVMValueRef valueRef = LLVMBuildAlloca(builderRef, UtilHander.getLLVMStruct("num", null), "result");
            LLVMValueRef elem     = LLVMBuildInBoundsGEP(builderRef, valueRef, UtilHander.firstElement, 2, "resultPtr");
            LLVMValueRef store    = LLVMBuildStore(builderRef, resultVal, elem);
            
            LLVMBuildRet(builderRef, valueRef);
        }
        
         */
        /*
        // set64
        {
            LLVMValueRef method = generateFunctionHeader(moduleRef, builderRef, "set64", "num", intint);
            
            LLVMValueRef LHS = LLVMGetParam(method, 0);
            LLVMValueRef RHS = LLVMGetParam(method, 1);
            
            LLVMValueRef LHSPtr = LLVMBuildInBoundsGEP(builderRef, LHS, UtilHander.firstElement, 2, "LHSPtr");
            LLVMValueRef RHSPtr = LLVMBuildInBoundsGEP(builderRef, RHS, UtilHander.firstElement, 2, "RHSPtr");
            
            LLVMValueRef LHSVal = LLVMBuildLoad(builderRef, LHSPtr, "LHSval");
            LLVMValueRef RHSVal = LLVMBuildLoad(builderRef, RHSPtr, "RHSval");
            
            LLVMBuildStore(builderRef, RHSVal, LHSPtr);
            LLVMBuildRet(builderRef, LHS);
        }
        // <
        
         */
        /*
        {
            LLVMValueRef method = generateFunctionHeader(moduleRef, builderRef, "cmpLS64", "bool", intint);
            
            LLVMValueRef LHS = LLVMGetParam(method, 0);
            LLVMValueRef RHS = LLVMGetParam(method, 1);
            
            LLVMValueRef LHSPtr = LLVMBuildInBoundsGEP(builderRef, LHS, UtilHander.firstElement, 2, "LHSPtr");
            LLVMValueRef RHSPtr = LLVMBuildInBoundsGEP(builderRef, RHS, UtilHander.firstElement, 2, "RHSPtr");
            
            LLVMValueRef LHSVal = LLVMBuildLoad(builderRef, LHSPtr, "LHSval");
            LLVMValueRef RHSVal = LLVMBuildLoad(builderRef, RHSPtr, "RHSval");
            
            LLVMValueRef resultVal = LLVMBuildICmp(builderRef, LLVMIntSLT, LHSVal, RHSVal, "cmpLS64");
            
            LLVMValueRef valueRef = LLVMBuildAlloca(builderRef, UtilHander.getLLVMStruct("bool", null), "result");
            LLVMValueRef elem     = LLVMBuildInBoundsGEP(builderRef, valueRef, UtilHander.firstElement, 2, "resultPtr");
            LLVMValueRef store    = LLVMBuildStore(builderRef, resultVal, elem);
            
            LLVMBuildRet(builderRef, valueRef);
        }
        
        
         */
        // >
        /*
        {
            LLVMValueRef method = generateFunctionHeader(moduleRef, builderRef, "cmpGT64", "bool", intint);
            
            LLVMValueRef LHS = LLVMGetParam(method, 0);
            LLVMValueRef RHS = LLVMGetParam(method, 1);
            
            LLVMValueRef LHSPtr = LLVMBuildInBoundsGEP(builderRef, LHS, UtilHander.firstElement, 2, "LHSPtr");
            LLVMValueRef RHSPtr = LLVMBuildInBoundsGEP(builderRef, RHS, UtilHander.firstElement, 2, "RHSPtr");
            
            LLVMValueRef LHSVal = LLVMBuildLoad(builderRef, LHSPtr, "LHSval");
            LLVMValueRef RHSVal = LLVMBuildLoad(builderRef, RHSPtr, "RHSval");
            
            LLVMValueRef resultVal = LLVMBuildICmp(builderRef, LLVMIntSGT, LHSVal, RHSVal, "cmpGT64");
            
            LLVMValueRef valueRef = LLVMBuildAlloca(builderRef, UtilHander.getLLVMStruct("bool", null), "result");
            LLVMValueRef elem     = LLVMBuildInBoundsGEP(builderRef, valueRef, UtilHander.firstElement, 2, "resultPtr");
            LLVMValueRef store    = LLVMBuildStore(builderRef, resultVal, elem);
            
            LLVMBuildRet(builderRef, valueRef);
        }
        
         */
    }
    
    private static PointerPointer buildPointerPointer(int arrayIndex, int parameterIndex)
    {
        return new PointerPointer(new LLVMValueRef[]{LLVMConstInt(LLVMInt32Type(), arrayIndex, 0), LLVMConstInt(LLVMInt32Type(), parameterIndex, 0)});
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
    
    public static LLVMValueRef getLLVMMethod(String methodName)
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
    
    public static LLVMValueRef getIntrinsic(String function)
    {
        return intrinsicFunctions.get(function);
    }
    
    public static Map<String, LLVMValueRef> getFunctions()
    {
        return functionsTable;
    }
    
}
