package semantic;

import ast.exprs.control.PrototypeParameter;
import div.Pair;

import java.util.*;

public class ParameterMap
{
    private String                     methodName;
    private List<Pair<String, String>> parameters;
    
    public ParameterMap(String name, List<PrototypeParameter> params)
    {
        this.methodName = name;
        this.parameters = new ArrayList<>();
        
        for (PrototypeParameter param : params)
        {
            Pair<String, String> data = new Pair<>(param.getType(), param.getName());
            this.parameters.add(data);
        }
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        ParameterMap that = (ParameterMap) o;
        return Objects.equals(methodName, that.methodName) &&
               Objects.equals(parameters, that.parameters);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(methodName, parameters);
    }
    
    @Override
    public String toString()
    {
        return parameters.toString();
    }
}
