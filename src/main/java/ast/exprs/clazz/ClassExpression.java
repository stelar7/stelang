package ast.exprs.clazz;


import ast.exprs.Expression;
import ast.exprs.basic.VariableExpression;
import ast.exprs.control.BlockExpression;

import java.util.List;

public class ClassExpression implements Expression
{
    private String                   classname;
    private String                   superClass;
    private BlockExpression          body;
    private List<VariableExpression> generic;
    
    public ClassExpression(String classname, BlockExpression body, String superClass, List<VariableExpression> generic)
    {
        this.superClass = superClass;
        this.classname = classname;
        this.body = body;
        this.generic = generic;
    }
    
    public String getSuperClass()
    {
        return superClass;
    }
    
    public String getClassname()
    {
        return classname;
    }
    
    public List<VariableExpression> getGenericParameters()
    {
        return generic;
    }
    
    public List<Expression> getBody()
    {
        return body.getBody();
    }
    
    @Override
    public String codegen()
    {
        return String.format("class %s extends %s {\n%s\n}", classname, superClass, body.codegen());
    }
}
