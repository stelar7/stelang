package ast.exprs.clazz;

import ast.exprs.control.*;

public class ConstructorExpression extends FunctionExpression
{
    public ConstructorExpression(String visibility, PrototypeExpression prototype, BlockExpression body)
    {
        super(visibility, prototype, body);
    }
    
    @Override
    public int getSortOrder()
    {
        return 4;
    }
}
