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
    private Lexer            lexer = new Lexer();
    
    public SemanticParser(SyntaxTree syntaxTree)
    {
        this.ast = syntaxTree.getAST();
        postProcess();
    }
    
    public void postProcess()
    {
        ast.addAll(generateDefaultTypeAST());
        Set<String>                                  types   = buildTypeList(ast, new HashSet<>(List.of("const", "var")));
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
        Map<String, Map<String, List<ParameterMap>>> returnData = new HashMap<>();
        
        List<ImportExpression> imports = new ArrayList<>();
        for (Expression e : ast)
        {
            //noinspection StatementWithEmptyBody
            if (e instanceof ImportExpression)
            {
                imports.add((ImportExpression) e);
            }
            
            if (e instanceof ClassExpression)
            {
                ClassExpression  c  = (ClassExpression) e;
                List<Expression> bl = c.getBody();
                
                Map<String, List<ParameterMap>> classMap = returnData.getOrDefault(c.getClassname(), new HashMap<>());
                for (Expression body : bl)
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
                        logSemanticError(String.format("%s \"%s\"%s already exists in class \"%s\"", func.getVisibility(), proto.getName(), self, c.getClassname()));
                    }
                    
                    methods.add(self);
                    classMap.put(func.getVisibility(), methods);
                }
                returnData.put(c.getClassname(), classMap);
            }
        }
        
        for (ImportExpression e : imports)
        {
            if (returnData.get(e.getClassname()) != null)
            {
                String     source     = Utils.readFileExternal(e.getLocation());
                SyntaxTree syntaxTree = new SyntaxTree(lexer.parse(source));
                returnData.putAll(buildMethodList(syntaxTree.getAST()));
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
            
            Map<String, String> typeMap = new HashMap<>();
            
            ClassExpression  c  = (ClassExpression) e;
            List<Expression> bl = c.getBody();
            
            bl.sort(Comparator.comparing(Expression::getSortOrder));
            
            for (Expression be : bl)
            {
                if (be instanceof FunctionExpression)
                {
                    validateFunction(c, (FunctionExpression) be, types, typeMap);
                }
                
                if (be instanceof VariableDefinitionExpression)
                {
                    validateVariableDefinition(be, types, null, typeMap);
                }
                
                if (be instanceof BinaryExpression)
                {
                    if (((BinaryExpression) be).getLeft() instanceof VariableDefinitionExpression)
                    {
                        validateVariableDefinition(((BinaryExpression) be).getLeft(), types, null, typeMap);
                    }
                }
            }
        }
    }
    
    private void validateVariableDefinition(Expression be, Set<String> types, PrototypeExpression proto, Map<String, String> typeMap)
    {
        VariableDefinitionExpression pe = ((VariableDefinitionExpression) be);
        
        if (typeMap.containsKey(pe.getIdentifier()))
        {
            logSemanticError(String.format("Variable named \"%s\" is already defined in scope", pe.getIdentifier()));
        }
        
        String extra = "class";
        if (proto != null)
        {
            extra = "method \"" + proto.getName() + "\"";
        }
        
        if (!types.contains(pe.getType()))
        {
            logSemanticError(String.format("Variable in %s \"%s\" has unknown return type: \"%s\"", extra, pe.getIdentifier(), pe.getType()));
        }
        
        if (!typeMap.containsKey(pe.getIdentifier()))
        {
            typeMap.put(pe.getIdentifier(), pe.getType());
        }
    }
    
    private void validateFunction(ClassExpression c, FunctionExpression be, Set<String> types, Map<String, String> typeMap)
    {
        PrototypeExpression pe = be.getPrototype();
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
        
        Map<String, String> functionTypes = new HashMap<>(typeMap);
        
        for (PrototypeParameter par : pe.getParameters())
        {
            if (!types.contains(par.getType()))
            {
                logSemanticError(String.format("%s \"%s\" in class \"%s\" has unknown type for parameter \"%s\": \"%s\"", type, pe.getName(), c.getClassname(), par.getName(), par.getType()));
            }
            
            if (functionTypes.containsKey(par.getName()))
            {
                logSemanticError(String.format("Parameter named \"%s\" is already defined in scope", par.getName()));
            }
            
            if (!functionTypes.containsKey(par.getName()))
            {
                functionTypes.put(par.getName(), par.getType());
            }
        }
        
        for (Expression ble : be.getBody())
        {
            if (ble instanceof VariableDefinitionExpression)
            {
                validateVariableDefinition(ble, types, pe, functionTypes);
            }
            
            if (ble instanceof BinaryExpression)
            {
                if (((BinaryExpression) ble).getLeft() instanceof VariableDefinitionExpression)
                {
                    validateVariableDefinition(((BinaryExpression) ble).getLeft(), types, pe, functionTypes);
                }
            }
            
            if (ble instanceof BlockExpression)
            {
                Map<String, String> blockTypes = new HashMap<>(functionTypes);
                for (Expression bex : ((BlockExpression) ble).getBody())
                {
                    if (bex instanceof VariableDefinitionExpression)
                    {
                        validateVariableDefinition(be, types, null, blockTypes);
                    }
                    
                    if (bex instanceof BinaryExpression)
                    {
                        if (((BinaryExpression) bex).getLeft() instanceof VariableDefinitionExpression)
                        {
                            validateVariableDefinition(((BinaryExpression) bex).getLeft(), types, null, blockTypes);
                        }
                    }
                }
            }
        }
    }
    
    private Set<String> buildTypeList(List<Expression> ast, Set<String> types)
    {
        List<ImportExpression> imports = new ArrayList<>();
        for (Expression e : ast)
        {
            if (e instanceof ImportExpression)
            {
                imports.add((ImportExpression) e);
            }
            
            if (e instanceof ClassExpression)
            {
                ClassExpression c = (ClassExpression) e;
                types.add(c.getClassname());
            }
        }
        
        for (ImportExpression e : imports)
        {
            if (!types.contains(e.getClassname()))
            {
                String     source     = Utils.readFileExternal(e.getLocation());
                SyntaxTree syntaxTree = new SyntaxTree(lexer.parse(source));
                types.addAll(buildTypeList(syntaxTree.getAST(), types));
            }
        }
        
        return types;
    }
    
    private void logSemanticError(String s)
    {
        System.err.println(s);
    }
}
