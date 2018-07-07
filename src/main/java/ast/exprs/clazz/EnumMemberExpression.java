package ast.exprs.clazz;

import ast.exprs.Expression;
import ast.exprs.basic.NullExpression;

import java.util.List;

public class EnumMemberExpression implements Expression
{
    private String           name;
    private List<Expression> params;
    
    public EnumMemberExpression(String name, List<Expression> params)
    {
        this.name = name;
        this.params = params;
    }
    
    @Override
    public String codegen()
    {
        return String.format("%s(%s),\n", name, params);
    }
    
    @Override
    public String toString()
    {
        return codegen();
    }
}
