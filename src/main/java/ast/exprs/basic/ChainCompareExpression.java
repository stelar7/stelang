package ast.exprs.basic;

import ast.exprs.Expression;
import lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

public class ChainCompareExpression implements Expression
{
    private List<Expression> exps;
    private Token            op;
    
    public ChainCompareExpression(List<Expression> exps, Token op)
    {
        this.exps = exps;
        this.op = op;
    }
    
    public Token getOperator()
    {
        return op;
    }
    
    public List<Expression> getExpressions()
    {
        return exps;
    }
    
    @Override
    public String codegen()
    {
        return exps.stream().map(Object::toString).collect(Collectors.joining(" " + op.getType().getTokenChars() + " "));
    }
}
