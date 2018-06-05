package ast.exprs.clazz;

import ast.exprs.Expression;
import ast.exprs.control.*;

import java.util.List;

public class OperatorExpression extends FunctionExpression
{
    public OperatorExpression(String visibility, PrototypeExpression prototype, List<Expression> body)
    {
        super(visibility, prototype, body);
    }
}
