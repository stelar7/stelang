package ast.exprs.control;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;

import static org.bytedeco.javacpp.LLVM.*;

public class BlockExpression extends ControlExpression
{
    private List<Expression> body;
    
    public BlockExpression(List<Expression> body)
    {
        this.body = body;
    }
    
    public List<Expression> getBody()
    {
        return body;
    }
    
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[1];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[2];
        
        LLVMBasicBlockRef entry = LLVMAppendBasicBlock(parent, "entry");
        LLVMPositionBuilderAtEnd(builder, entry);
        
        body.forEach(b -> b.codegen(obj));
        return null;
    }
}
