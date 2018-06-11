package semantic;

import ast.SyntaxTree;
import ast.exprs.Expression;
import ast.exprs.basic.BinaryExpression;
import ast.exprs.clazz.*;
import ast.exprs.control.*;
import ast.exprs.div.*;
import div.Utils;
import lexer.*;

import java.util.*;

public class SemanticParser
{
    private List<Expression> ast;
    
    public SemanticParser(SyntaxTree syntaxTree)
    {
        this.ast = syntaxTree.getAST();
        postProcess();
    }
    
    public void postProcess()
    {
        ast.addAll(generateDefaultTypeAST());
        Set<String>                                  types   = buildTypeList(ast);
        Map<String, Map<String, List<ParameterMap>>> methods = buildMethodList(ast);
        validateTypes(ast, types);
    }
    
    private Collection<? extends Expression> generateDefaultTypeAST()
    {
        List<Expression> expressions = new ArrayList<>();
        Lexer            lexer       = new Lexer();
        
        List<String> files = Utils.readFolder("defaults");
        for (String file : files)
        {
            String      data       = Utils.readFile("defaults/" + file);
            List<Token> tokens     = lexer.parse(data);
            SyntaxTree  syntaxTree = new SyntaxTree(tokens);
            
            expressions.addAll(syntaxTree.getAST());
        }
        
        return expressions;
    }
    
    
    private Map<String, Map<String, List<ParameterMap>>> buildMethodList(List<Expression> ast)
    {
        // map <class, map <visibility, parameterMap>>
        Map<String, Map<String, List<ParameterMap>>> returnData = new HashMap<>();
        
        for (Expression e : ast)
        {
            //noinspection StatementWithEmptyBody
            if (e instanceof ImportExpression)
            {
                // TODO: read the class file, and load those methods aswell
            }
            
            if (e instanceof ClassExpression)
            {
                ClassExpression c = (ClassExpression) e;
                
                if (!(c.getBody() instanceof BlockExpression))
                {
                    continue;
                }
                
                Map<String, List<ParameterMap>> classMap = returnData.getOrDefault(c.getClassname(), new HashMap<>());
                BlockExpression                 bl       = (BlockExpression) c.getBody();
                for (Expression body : bl.getBody())
                {
                    if (!(body instanceof FunctionExpression))
                    {
                        continue;
                    }
                    
                    FunctionExpression  func  = (FunctionExpression) body;
                    PrototypeExpression proto = func.getPrototype();
                    
                    List<ParameterMap> methods = classMap.getOrDefault(func.getVisibility(), new ArrayList<>());
                    ParameterMap       self    = new ParameterMap(proto.getName(), proto.getParameters());
                    
                    if (methods.contains(self))
                    {
                        logSemanticError(String.format("Method \"%s\" (parameters %s) already exists in class \"%s\"", proto.getName(), self, c.getClassname()));
                    }
                    
                    methods.add(self);
                    classMap.put(func.getVisibility(), methods);
                }
                returnData.put(c.getClassname(), classMap);
            }
        }
        
        return returnData;
    }
    
    
    private void validateTypes(List<Expression> ast, Set<String> types)
    {
        for (Expression e : ast)
        {
            if (!(e instanceof ClassExpression))
            {
                continue;
            }
            
            ClassExpression c = (ClassExpression) e;
            if (!(c.getBody() instanceof BlockExpression))
            {
                continue;
            }
            
            BlockExpression b = (BlockExpression) c.getBody();
            for (Expression be : b.getBody())
            {
                if (be instanceof FunctionExpression)
                {
                    validateFunction(c, be, types);
                }
                
                if (be instanceof VariableDefinitionExpression)
                {
                    validateVariable(be, types, null);
                }
                
                if (be instanceof BinaryExpression)
                {
                    if (((BinaryExpression) be).getLeft() instanceof VariableDefinitionExpression)
                    {
                        validateVariable(((BinaryExpression) be).getLeft(), types, null);
                    }
                }
            }
        }
    }
    
    private void validateVariable(Expression be, Set<String> types, PrototypeExpression proto)
    {
        VariableDefinitionExpression pe = ((VariableDefinitionExpression) be);
        
        if (pe.getVisibility().equals("const") || pe.getVisibility().equals("val"))
        {
            return;
        }
        
        String extra = "class";
        if (proto != null)
        {
            extra = "method \"" + proto.getName() + "\"";
        }
        
        if (!types.contains(pe.getVisibility()))
        {
            logSemanticError(String.format("Variable in %s \"%s\" has unknown return type: \"%s\"", extra, pe.getIdentifier(), pe.getVisibility()));
        }
        
    }
    
    private void validateFunction(ClassExpression c, Expression be, Set<String> types)
    {
        PrototypeExpression pe = ((FunctionExpression) be).getPrototype();
        if (!types.contains(pe.getReturnType()))
        {
            logSemanticError(String.format("Function \"%s\" has unknown return type: \"%s\"", pe.getName(), pe.getReturnType()));
        }
        
        String type = "Function";
        if (be instanceof ConstructorExpression)
        {
            type = "Constructor";
        }
        
        if (be instanceof OperatorExpression)
        {
            type = "Operator";
        }
        
        for (PrototypeParameter par : pe.getParameters())
        {
            if (!types.contains(par.getType()))
            {
                logSemanticError(String.format("%s \"%s\" in class \"%s\" has unknown type for parameter \"%s\": \"%s\"", type, pe.getName(), c.getClassname(), par.getName(), par.getType()));
            }
        }
        
        if (((FunctionExpression) be).getBody() instanceof BlockExpression)
        {
            BlockExpression bl = (BlockExpression) ((FunctionExpression) be).getBody();
            for (Expression ble : bl.getBody())
            {
                if (ble instanceof VariableDefinitionExpression)
                {
                    validateVariable(ble, types, pe);
                }
                
                if (ble instanceof BinaryExpression)
                {
                    if (((BinaryExpression) ble).getLeft() instanceof VariableDefinitionExpression)
                    {
                        validateVariable(((BinaryExpression) ble).getLeft(), types, pe);
                    }
                }
            }
        }
        
    }
    
    private Set<String> buildTypeList(List<Expression> ast)
    {
        Set<String> types = new HashSet<>();
        for (Expression e : ast)
        {
            if (e instanceof ImportExpression)
            {
                ImportExpression i = (ImportExpression) e;
                types.add(i.getClassname());
                // TODO: read the class file, and load those types aswell
            }
            
            if (e instanceof ClassExpression)
            {
                ClassExpression c = (ClassExpression) e;
                types.add(c.getClassname());
            }
            
        }
        
        return types;
    }
    
    private void logSemanticError(String s)
    {
        System.err.println(s);
    }
}
