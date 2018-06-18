package ast.exprs.clazz;

import ast.exprs.Expression;
import ast.exprs.control.*;

import java.security.cert.CollectionCertStoreParameters;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionExpression extends ControlExpression
{
    private String              visibility;
    private PrototypeExpression prototype;
    private BlockExpression     body;
    
    public FunctionExpression(String visibility, PrototypeExpression prototype, BlockExpression body)
    {
        this.visibility = visibility;
        this.prototype = prototype;
        this.body = body;
    }
    
    public String getVisibility()
    {
        return visibility;
    }
    
    public PrototypeExpression getPrototype()
    {
        return prototype;
    }
    
    public void setPrototype(PrototypeExpression prototype)
    {
        this.prototype = prototype;
    }
    
    public List<Expression> getBody()
    {
        return body.getBody();
    }
    
    public BlockExpression getBlock()
    {
        return body;
    }
    
    @Override
    public String codegen()
    {
        return String.format("function %s(%s) {\n\t%s\n}", prototype.getName(), prototype.getParameters().stream().map(PrototypeParameter::getType).collect(Collectors.joining(",")), body.codegen());
    }
    
}
