package ast.exprs.control;

import ast.exprs.Expression;
import ast.exprs.basic.*;

import java.util.List;
import java.util.stream.Collectors;

public class CastExpression extends ControlExpression
{
    private List<Expression> casts;
    private BlockExpression  block;
    
    public CastExpression(List<Expression> casts, BlockExpression block)
    {
        this.casts = casts;
        this.block = block;
    }
    
    public List<Expression> getCasts()
    {
        return casts;
    }
    
    public BlockExpression getBlock()
    {
        return block;
    }
    
    @Override
    public String codegen()
    {
        return casts.stream()
                    .map(c -> (BinaryExpression) c)
                    .map(c -> String.format("%s:%s = %s",
                                            ((VariableDefinitionExpression) c.getLeft()).getType(),
                                            ((VariableDefinitionExpression) c.getLeft()).getIdentifier(),
                                            ((VariableExpression) c.getRight()).getName())
                        ).collect(Collectors.joining());
    }
}
