package ast.exprs.control;

import ast.exprs.Expression;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;

public class SwitchExpression implements Expression
{
    private List<SwitchParameter> cases;
    private SwitchParameter       defaultParam;
    
    public SwitchExpression(List<SwitchParameter> cases, SwitchParameter defaultParam)
    {
        this.cases = cases;
        this.defaultParam = defaultParam;
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMValueRef   parent  = (LLVMValueRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        return "switch {}";
    }
}

