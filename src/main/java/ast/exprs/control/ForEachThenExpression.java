package ast.exprs.control;

import ast.exprs.Expression;
import ast.exprs.clazz.ClassExpression;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;

public class ForEachThenExpression extends ForEachExpression
{
    private Expression thenStatements;
    
    public ForEachThenExpression(List<Expression> init, Expression collection, Expression doStatements, Expression thenStatements)
    {
        super(init, collection, doStatements);
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
