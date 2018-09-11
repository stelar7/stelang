package ast.exprs.clazz;

import ast.exprs.Expression;
import ast.exprs.clazz.ClassExpression;
import org.bytedeco.javacpp.LLVM.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.bytedeco.javacpp.LLVM.*;

public class ClassBlockExpression implements Expression
{
    private List<Expression> body;
    
    public ClassBlockExpression(List<Expression> body)
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
        body.forEach(b -> b.codegen(obj));
        return null;
    }
}
