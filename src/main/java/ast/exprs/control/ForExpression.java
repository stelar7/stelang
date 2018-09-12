package ast.exprs.control;

import ast.exprs.Expression;
import ast.exprs.clazz.ClassExpression;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;

public class ForExpression extends ControlExpression
{
    private List<Expression> init;
    private List<Expression> condition;
    private List<Expression> increment;
    private Expression       doStatements;
    
    public ForExpression(List<Expression> init, List<Expression> condition, List<Expression> increment, Expression doStatements)
    {
        this.init = init;
        this.condition = condition;
        this.increment = increment;
        this.doStatements = doStatements;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[2];
        
        return null;
    }
}
