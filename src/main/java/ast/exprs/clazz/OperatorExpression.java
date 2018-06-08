package ast.exprs.clazz;

import ast.exprs.Expression;
import ast.exprs.control.*;

public class OperatorExpression extends FunctionExpression
{
    public OperatorExpression(String visibility, PrototypeExpression prototype, Expression body)
    {
        super(visibility, prototype, body);
    }
}
