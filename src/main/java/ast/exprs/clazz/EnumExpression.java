package ast.exprs.clazz;


import ast.exprs.Expression;
import ast.exprs.control.BlockExpression;

import java.util.List;

public class EnumExpression extends ClassExpression
{
    private List<Expression> members;
    
    public EnumExpression(String classname, List<Expression> members, BlockExpression body)
    {
        super(classname, body);
        this.members = members;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
