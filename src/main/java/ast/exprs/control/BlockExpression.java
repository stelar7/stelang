package ast.exprs.control;

import ast.exprs.Expression;
import ast.exprs.clazz.ClassExpression;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;
import java.util.stream.Collectors;

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
    
    public Object codegen(Object... obj)
    {
        /*
        LLVMBasicBlockRef entry = LLVMAppendBasicBlock(LLVMConstNull(LLVMVoidType()), "entry");
        LLVMPositionBuilderAtEnd(builder, entry);
        */
        body.forEach(b -> b.codegen(obj));
        return null;
    }
}
