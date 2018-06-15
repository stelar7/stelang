package ast.exprs.clazz;

import ast.exprs.control.*;

public class OperatorExpression extends FunctionExpression
{
    public OperatorExpression(String visibility, PrototypeExpression prototype, BlockExpression body)
    {
        super(visibility, prototype, body);
    }
    
    @Override
    public int getSortOrder()
    {
        return 3;
    }
}
