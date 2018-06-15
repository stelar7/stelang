package ast.exprs.clazz;


import ast.exprs.Expression;
import ast.exprs.control.BlockExpression;

import java.util.List;

public class EnumExpression extends ClassExpression
{
    private List<Expression> members;
    
    public EnumExpression(String classname, String superClass, List<Expression> members, BlockExpression body)
    {
        super(classname, body, superClass);
        this.members = members;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
