package ast.exprs.div;

import ast.exprs.Expression;

public class TextExpression extends Expression
{
    String content;
    
    public TextExpression(String content)
    {
        this.content = content;
    }
    
    @Override
    public String codegen()
    {
        return null;
    }
}
