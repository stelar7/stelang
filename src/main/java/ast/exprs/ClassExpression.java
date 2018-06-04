package ast.exprs;


import java.util.List;

public class ClassExpression extends Expression
{
    private String           classname;
    private List<Expression> body;
    
    public ClassExpression(String classname, List<Expression> body)
    {
        this.classname = classname;
        this.body = body;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
