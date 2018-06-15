package ast.exprs;

public interface Expression
{
    String codegen();
    
    default int getSortOrder()
    {
        return 10;
    }
}
