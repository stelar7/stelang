package ast.exprs.control;

import ast.exprs.Expression;
import ast.exprs.clazz.ClassExpression;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;

public class DoWhileThenExpression extends DoWhileExpression
{
    private List<Expression> thenStatements;
    
    public DoWhileThenExpression(Expression condition, List<Expression> doStatements, List<Expression> thenStatements)
    {
        super(condition, doStatements);
        this.thenStatements = thenStatements;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        return null;
    }
}
