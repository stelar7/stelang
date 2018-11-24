package ast.exprs.clazz;


import ast.exprs.Expression;
import ast.exprs.basic.*;
import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.LLVM.LLVMModuleRef;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.bytedeco.javacpp.LLVM.*;

public class ClassExpression implements Expression
{
    private String               classname;
    private String               superClass;
    private ClassBlockExpression body;
    
    public ClassExpression(String classname, ClassBlockExpression body, String superClass)
    {
        this.superClass = superClass;
        this.classname = classname;
        this.body = body;
    }
    
    
    public String getSuperClass()
    {
        return superClass;
    }
    
    public String getClassname()
    {
        return classname;
    }
    
    public List<Expression> getBody()
    {
        return body.getBody();
    }
    
    @Override
    public Object codegen(Object... obj)
    {
        LLVMModuleRef  module  = (LLVMModuleRef) obj[0];
        LLVMBuilderRef builder = (LLVMBuilderRef) obj[1];
        
        LLVMTypeRef classRef = UtilHander.getLLVMStruct(classname, this);
        body.codegen(module, builder, classname);
        FunctionExpression.bodies.forEach(Supplier::get);
        FunctionExpression.bodies.clear();
        
        return null;
    }
    
    public LLVMTypeRef[] getParameterTypes()
    {
        List<Expression> vars = getBody().stream().filter(b -> (b instanceof VariableDefinitionExpression)).collect(Collectors.toList());
        
        LLVMTypeRef[] vals = new LLVMTypeRef[vars.size()];
        for (int i = 0; i < vals.length; i++)
        {
            VariableDefinitionExpression var = (VariableDefinitionExpression) vars.get(i);
            vals[i] = UtilHander.getLLVMStruct(var.getType(), null);
        }
        return vals;
    }
}
