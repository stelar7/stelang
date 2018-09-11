package ast.exprs.clazz;


import ast.exprs.Expression;
import ast.exprs.basic.VariableExpression;
import ast.exprs.control.BlockExpression;
import ast.exprs.util.UtilHander;
import org.bytedeco.javacpp.LLVM.LLVMModuleRef;

import java.util.List;

import static org.bytedeco.javacpp.LLVM.*;

public class ClassExpression implements Expression
{
    private String                   classname;
    private String                   superClass;
    private ClassBlockExpression     body;
    private List<VariableExpression> generic;
    
    public ClassExpression(String classname, ClassBlockExpression body, String superClass, List<VariableExpression> generic)
    {
        this.superClass = superClass;
        this.classname = classname;
        this.body = body;
        this.generic = generic;
    }
    
    
    public String getSuperClass()
    {
        return superClass;
    }
    
    public String getClassname()
    {
        return classname;
    }
    
    public List<VariableExpression> getGenericParameters()
    {
        return generic;
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
        generic.forEach(variableExpression -> variableExpression.codegen(classRef, builder, module));
        body.codegen(module, builder, classRef);
        
        return null;
    }
    
    public LLVMTypeRef[] getParameterTypes()
    {
        LLVMTypeRef[] vals = new LLVMTypeRef[generic.size()];
        for (int i = 0; i < generic.size(); i++)
        {
            vals[i] = UtilHander.getLLVMStruct((UtilHander.lookupVariableType(generic.get(i).getName())), null);
        }
        return vals;
    }
}
