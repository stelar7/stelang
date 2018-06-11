package ast.exprs.clazz;

import ast.exprs.Expression;
import ast.exprs.control.*;

public class ConstructorExpression extends FunctionExpression
{
    public ConstructorExpression(String visibility, PrototypeExpression prototype, Expression body)
    {
        super(visibility, prototype, body);
    }
}
