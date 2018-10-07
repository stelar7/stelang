import ast.*;
import ast.exprs.Expression;
import ast.exprs.util.UtilHander;
import div.Utils;
import lexer.*;
import org.bytedeco.javacpp.*;
import semantic.SemanticParser;

import java.util.*;

import static org.bytedeco.javacpp.LLVM.*;

public class FullTest
{
    
    public static void main(String[] args)
    {
        String filename = "test_simple.st7";
        String data     = Utils.readFile(filename);
        
        Lexer          lexer      = new Lexer();
        List<Token>    tokens     = lexer.parse(filename, data);
        SyntaxTree     syntaxTree = new SyntaxTree(tokens);
        SemanticParser semantics  = new SemanticParser(syntaxTree);
        
        LLVMBuilderRef builder = LLVMCreateBuilder();
        LLVMModuleRef  module  = LLVMModuleCreateWithName("test_module");
        semantics.preInit(module, builder);
        semantics.codegen(module, builder);
        
        BytePointer error = new BytePointer((Pointer) null); // Used to retrieve messages from functions
        LLVMLinkInMCJIT();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeDisassembler();
        LLVMInitializeNativeTarget();
    
        LLVMPrintModuleToFile(module, "test", error);
        LLVMVerifyModule(module, LLVMAbortProcessAction, error);
        LLVMDisposeMessage(error); // Handler == LLVMAbortProcessAction -> No need to check errors
        
        
        LLVMExecutionEngineRef engine = new LLVMExecutionEngineRef();
        if (LLVMCreateJITCompilerForModule(engine, module, 2, error) != 0)
        {
            System.err.println(error.getString());
            LLVMDisposeMessage(error);
            System.exit(-1);
        }
        
        LLVMPassManagerRef pass = LLVMCreatePassManager();
        LLVMAddConstantPropagationPass(pass);
        LLVMAddInstructionCombiningPass(pass);
        LLVMAddPromoteMemoryToRegisterPass(pass);
        // LLVMAddDemoteMemoryToRegisterPass(pass); // Demotes every possible value to memory
        LLVMAddGVNPass(pass);
        LLVMAddCFGSimplificationPass(pass);
        //LLVMRunPassManager(pass, module);
        //LLVMDumpModule(module);
        LLVMPrintModuleToFile(module, "test", error);
        
        LLVMGenericValueRef exec_args = LLVMCreateGenericValueOfInt(LLVMInt32Type(), 10, 0);
        LLVMGenericValueRef exec_res  = LLVMRunFunction(engine, UtilHander.getMainMethod(), 0, exec_args);
        System.err.println();
        System.err.format("; Running %s...%n", UtilHander.mainMethodName);
        System.err.println("; Result: " + LLVMGenericValueToInt(exec_res, 0));
        
        LLVMDisposePassManager(pass);
        LLVMDisposeBuilder(builder);
        LLVMDisposeExecutionEngine(engine);
        
    }
}
