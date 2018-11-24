package ast.exprs.control;

import ast.exprs.Expression;
import ast.exprs.clazz.ClassExpression;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.bytedeco.javacpp.LLVM.*;

public class IfExpression extends ControlExpression
{
    private IfContitionExpression condition;
    private List<Expression>      trueExpressions;
    private List<Expression>      falseExpressions;
    
    public IfExpression(IfContitionExpression condition, List<Expression> trueExpressions, List<Expression> falseExpressions)
    {
        this.condition = condition;
        this.trueExpressions = trueExpressions;
        this.falseExpressions = falseExpressions;
    }
    
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[2];
        
        LLVMValueRef value  = (LLVMValueRef) condition.codegen(obj);
        LLVMValueRef ifStmt = LLVMBuildICmp(builder, LLVMIntUGE, value, LLVMConstInt(LLVMInt32Type(), 1, 0), "value >= 1");
    
        LLVMBasicBlockRef ifTrue  = LLVMAppendBasicBlock(parent, "ifTrue");
        LLVMBasicBlockRef ifFalse = LLVMAppendBasicBlock(parent, "ifFalse");
        LLVMBasicBlockRef end     = LLVMAppendBasicBlock(parent, "end");
        
        LLVMBuildCondBr(builder, ifStmt, ifTrue, ifFalse);
        
        LLVMPositionBuilderAtEnd(builder, ifTrue);
        trueExpressions.forEach(t -> t.codegen(obj));
        // only break if no return expr..
        LLVMBuildBr(builder, end);
        
        LLVMPositionBuilderAtEnd(builder, ifFalse);
        falseExpressions.forEach(f -> f.codegen(obj));
        // only break if no return expr..
        LLVMBuildBr(builder, end);
        
        LLVMPositionBuilderAtEnd(builder, end);
        return null;
    }
    
    public IfContitionExpression getCondition()
    {
        return condition;
    }
    
    public List<Expression> getTrueExpressions()
    {
        return trueExpressions;
    }
    
    public List<Expression> getFalseExpressions()
    {
        return falseExpressions;
    }
}
