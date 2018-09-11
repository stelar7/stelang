package ast.exprs.control;

import ast.exprs.Expression;
import ast.exprs.clazz.ClassExpression;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;

public class ForEachExpression extends ControlExpression
{
    private List<Expression> init;
    private Expression       collection;
    private Expression       doStatements;
    
    public ForEachExpression(List<Expression> init, Expression collection, Expression doStatements)
    {
        this.init = init;
        this.collection = collection;
        this.doStatements = doStatements;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        return null;
    }
}
