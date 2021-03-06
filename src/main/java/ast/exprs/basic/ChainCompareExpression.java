package ast.exprs.basic;

import ast.exprs.Expression;
import ast.exprs.util.UtilHander;
import lexer.Token;
import org.bytedeco.javacpp.LLVM.*;
import org.bytedeco.javacpp.PointerPointer;

import java.util.List;
import java.util.stream.Collectors;

import static org.bytedeco.javacpp.LLVM.*;

public class ChainCompareExpression implements Expression
{
    private List<Expression> exps;
    private Token            op;
    
    public ChainCompareExpression(List<Expression> exps, Token op)
    {
        this.exps = exps;
        this.op = op;
    }
    
    public Token getOperator()
    {
        return op;
    }
    
    public List<Expression> getExpressions()
    {
        return exps;
    }
    
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[2];
        
        LLVMValueRef current = null;
        for (int i = 0; i < exps.size() - 1; i++)
        {
            Expression left  = exps.get(i);
            Expression right = exps.get(i + 1);
            
            LLVMValueRef leftCode  = (LLVMValueRef) left.codegen(obj);
            LLVMValueRef rightCode = (LLVMValueRef) right.codegen(obj);
            
            LLVMValueRef   method       = UtilHander.getLLVMMethod("STL1<24self3num5other3num4bool");
            LLVMValueRef[] call_op_args = {leftCode, rightCode};
            
            //LLVMValueRef   call_op      = LLVMBuildCall(builder, method, new PointerPointer(call_op_args), 2, String.format("a %s b", op.getContent()));
            //LLVMValueRef ifStmt = LLVMBuildICmp(builder, LLVMIntEQ, call_op, LLVMConstInt(LLVMInt1Type(), 1, 0), "comparisonResult");
            
            LLVMValueRef ifStmt = LLVMBuildCall(builder, method, new PointerPointer(call_op_args), 2, String.format("a %s b", op.getContent()));
            
            if (current == null)
            {
                current = ifStmt;
            } else
            {
                current = LLVMBuildAnd(builder, current, ifStmt, "chain");
            }
        }
        
        return current;
    }
}
