package ast.exprs.div;

import ast.exprs.Expression;
import lexer.*;
import org.bytedeco.javacpp.LLVM.*;

public class PostOpExpression implements Expression
{
    private Expression variable;
    private TokenType  operator;
    
    public PostOpExpression(Expression variable, TokenType currentToken)
    {
        this.variable = variable;
        this.operator = currentToken.toFirstToken();
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        
        
        String var = (String) variable.codegen();
        
        return String.format("%s = %s %s 1", var, var, operator);
    }
}
