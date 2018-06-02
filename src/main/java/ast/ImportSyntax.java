package ast;

import ast.Syntax;

public class ImportSyntax extends Syntax
{
    private String classname;
    private String location;
    
    public ImportSyntax(String classname, String location)
    {
        this.classname = classname;
        this.location = location;
    }
}
