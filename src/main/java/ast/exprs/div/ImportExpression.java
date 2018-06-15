package ast.exprs.div;

import ast.exprs.Expression;

public class ImportExpression implements Expression
{
    private String classname;
    private String location;
    
    public ImportExpression(String classname, String location)
    {
        this.classname = classname;
        this.location = location;
    }
    
    public String getClassname()
    {
        return classname;
    }
    
    public String getLocation()
    {
        return location;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
