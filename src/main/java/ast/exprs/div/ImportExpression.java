package ast.exprs.div;

import ast.exprs.Expression;

public class ImportExpression extends Expression
{
    private String classname;
    private String location;
    
    public ImportExpression(String classname, String location)
    {
        this.classname = classname;
        this.location = location;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
