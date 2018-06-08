package ast.exprs.clazz;


import ast.exprs.Expression;

import java.util.List;

public class ClassExpression extends Expression
{
    private String     classname;
    private Expression body;
    
    public ClassExpression(String classname, Expression body)
    {
        this.classname = classname;
        this.body = body;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
