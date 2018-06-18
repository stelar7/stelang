package ast.exprs.control;

import ast.exprs.Expression;

import java.util.List;

public class SwitchExpression implements Expression
{
    private List<SwitchParameter> cases;
    private SwitchParameter       defaultParam;
    
    public SwitchExpression(List<SwitchParameter> cases, SwitchParameter defaultParam)
    {
        this.cases = cases;
        this.defaultParam = defaultParam;
    }
    
    @Override
    public String codegen()
    {
        return "switch {}";
    }
}

