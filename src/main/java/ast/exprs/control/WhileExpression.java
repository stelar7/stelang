package ast.exprs.control;

import ast.exprs.Expression;
import ast.exprs.clazz.ClassExpression;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;

public class WhileExpression extends ControlExpression
{
    private Expression       condition;
    private List<Expression> doStatement;
    
    public WhileExpression(Expression condition, List<Expression> doStatement)
    {
        this.condition = condition;
        this.doStatement = doStatement;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        return null;
    }
}
