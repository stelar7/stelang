package semantic;

import ast.exprs.Expression;
import ast.exprs.control.PrototypeExpression;
import lexer.Token;

import java.util.*;

public class TypeMapList
{
    List<TypeMap> data = new ArrayList<>();
    
    public boolean contains(String type)
    {
        List<String> ignored = List.of("const", "var");
        if (ignored.contains(type))
        {
            return true;
        }
        
        for (TypeMap datum : data)
        {
            if (datum.type.equals(type))
            {
                return true;
            }
        }
        return false;
    }
    
    public void add(TypeMap item)
    {
        data.add(item);
    }
    
    public TypeMap get(String classname)
    {
        for (TypeMap datum : data)
        {
            if (datum.type.equals(classname))
            {
                return datum;
            }
        }
        return null;
    }
    
    static class TypeMap
    {
        public TypeMap(String type)
        {
            this.type = type;
        }
        
        private String type;
        List<PrototypeExpression> operators = new ArrayList<>();
        List<PrototypeExpression> functions = new ArrayList<>();
        List<PrototypeExpression> globals   = new ArrayList<>();
        List<PrototypeExpression> pures     = new ArrayList<>();
        List<Expression>          generics  = new ArrayList<>();
        
        public boolean hasOperator(Token op)
        {
            for (PrototypeExpression operator : operators)
            {
                if (operator.getName().equals(op.getContent()))
                {
                    return true;
                }
            }
            return false;
        }
    }
}