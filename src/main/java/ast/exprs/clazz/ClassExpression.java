package ast.exprs.clazz;


import ast.exprs.Expression;
import ast.exprs.control.BlockExpression;

import java.util.List;

public class ClassExpression extends Expression
{
    private String          classname;
    private BlockExpression body;
    
    public ClassExpression(String classname, BlockExpression body)
    {
        this.classname = classname;
        this.body = body;
    }
    
    public String getClassname()
    {
        return classname;
    }
    
    public List<Expression> getBody()
    {
        return body.getBody();
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
