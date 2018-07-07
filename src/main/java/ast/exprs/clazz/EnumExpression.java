package ast.exprs.clazz;


import ast.exprs.Expression;
import ast.exprs.control.BlockExpression;

import java.util.List;
import java.util.stream.Collectors;

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
        return String.format("enum %s extends %s \n{\n%s\n%s\n}", getClassname(), getSuperClass(), members.stream().map(Object::toString).collect(Collectors.joining()), getBody());
    }
}
