package ast.exprs.basic;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;
import java.util.stream.Collectors;

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
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        
        return String.format("call %s %s", methodName, arguments.stream().map(Expression::toString).collect(Collectors.joining(",")));
    }
}
