package ast;

import java.util.List;

public class ClassSyntax extends Syntax
{
    private String       classname;
    private List<Syntax> body;
    
    public ClassSyntax(String classname, List<Syntax> body)
    {
        this.classname = classname;
        this.body = body;
    }
}
