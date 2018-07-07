package ast.exprs.basic;

import ast.exprs.Expression;

public class TextExpression implements Expression
{
    String content;
    
    public TextExpression(String content)
    {
        this.content = content;
    }
    
    @Override
    public String codegen()
    {
        return "\"" + content + "\"";
    }
    
    @Override
    public String toString()
    {
        return codegen();
    }
}
