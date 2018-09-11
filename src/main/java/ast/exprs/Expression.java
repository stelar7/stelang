package ast.exprs;

public interface Expression
{
    Object codegen(Object... parent);
    
    default int getSortOrder()
    {
        return 10;
    }
}
