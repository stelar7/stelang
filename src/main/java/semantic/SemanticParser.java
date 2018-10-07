package semantic;

import ast.SyntaxTree;
import ast.exprs.Expression;
import ast.exprs.basic.*;
import ast.exprs.clazz.*;
import ast.exprs.control.*;
import ast.exprs.div.*;
import ast.exprs.util.UtilHander;
import div.Utils;
import lexer.*;
import org.bytedeco.javacpp.LLVM.*;
import semantic.TypeMapList.TypeMap;

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
    
    private void postProcess()
    {
        ast.addAll(generateDefaultTypeAST());
        TypeMapList                                  types   = buildTypeList(ast, new TypeMapList());
        Map<String, Map<String, List<ParameterMap>>> methods = buildMethodList(ast);
        validateTypes(ast, types);
    }
    
    public void preInit(LLVMModuleRef module, LLVMBuilderRef builder)
    {
        UtilHander.computeLLVMStructs();
        generateDefaultTypeAST().forEach(e -> e.codegen(module, builder));
    }
    
    public void codegen(LLVMModuleRef module, LLVMBuilderRef builder)
    {
        List<Expression> astLocal = new ArrayList<>(ast);
        astLocal.removeAll(generateDefaultTypeAST());
        astLocal.forEach(e -> e.codegen(module, builder));
    }
    
    
    private Collection<? extends Expression> generateDefaultTypeAST()
    {
        List<Expression> expressions = new ArrayList<>();
        Lexer            lexer       = new Lexer();
        
        List<String> files = Utils.readFolder("defaults");
        for (String file : files)
        {
            String      filename   = "defaults/" + file;
            String      data       = Utils.readFile(filename);
            List<Token> tokens     = lexer.parse(filename, data);
            SyntaxTree  syntaxTree = new SyntaxTree(tokens);
            
            expressions.addAll(syntaxTree.getAST());
        }
        return expressions;
    }
    
    
    private Map<String, Map<String, List<ParameterMap>>> buildMethodList(List<Expression> ast)
    {
        Map<String, Map<String, List<ParameterMap>>> returnData = new HashMap<>();
        boolean                                      hasMain    = false;
        
        
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
                    
                    if (proto.getName().equals(UtilHander.mainMethodName))
                    {
                        hasMain = true;
                    }
                }
                returnData.put(c.getClassname(), classMap);
            }
        }
        
        for (ImportExpression e : imports)
        {
            if (returnData.get(e.getClassname()) != null)
            {
                String     source     = Utils.readFileExternal(e.getLocation());
                SyntaxTree syntaxTree = new SyntaxTree(lexer.parse(e.getLocation(), source));
                returnData.putAll(buildMethodList(syntaxTree.getAST()));
            }
        }
        
        if (!hasMain)
        {
            System.err.println("No main method found");
            System.exit(0);
        }
        
        return returnData;
    }
    
    
    private void validateTypes(List<Expression> ast, TypeMapList types)
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
                    validateFunction(c, (FunctionExpression) be, types, c, typeMap);
                }
                
                if (be instanceof VariableDefinitionExpression)
                {
                    validateVariableDefinition(be, types, null, c, typeMap);
                }
                
                if (be instanceof BinaryExpression)
                {
                    validateBinaryExpression((BinaryExpression) be, types, null, c, typeMap);
                }
                
                if (be instanceof ChainCompareExpression)
                {
                    validateChainCompareExpression((ChainCompareExpression) be, types, null, c, typeMap);
                }
                
                if (be instanceof BlockExpression)
                {
                    validateBlock((BlockExpression) be, types, null, c, typeMap);
                }
            }
        }
    }
    
    private void validateVariableDefinition(Expression be, TypeMapList types, PrototypeExpression proto, ClassExpression c, Map<String, String> typeMap)
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
    
    private void validateBlock(BlockExpression ble, TypeMapList knownTypes, PrototypeExpression pe, ClassExpression c, Map<String, String> usedVariables)
    {
        for (Expression bex : ble.getBody())
        {
            if (bex instanceof VariableDefinitionExpression)
            {
                validateVariableDefinition(bex, knownTypes, pe, c, usedVariables);
            }
            
            if (bex instanceof BinaryExpression)
            {
                validateBinaryExpression(((BinaryExpression) bex), knownTypes, pe, c, usedVariables);
            }
            
            if (bex instanceof ChainCompareExpression)
            {
                validateChainCompareExpression((ChainCompareExpression) bex, knownTypes, pe, c, usedVariables);
            }
            
            if (bex instanceof BlockExpression)
            {
                validateBlock((BlockExpression) bex, knownTypes, pe, c, new HashMap<>(usedVariables));
            }
            
            if (bex instanceof CallExpression)
            {
                validateCallExpression((CallExpression) bex, knownTypes, pe, c, usedVariables);
            }
            
            if (bex instanceof ReturnExpression)
            {
                validateReturnExpression((ReturnExpression) bex, knownTypes, pe, c, usedVariables);
            }
            
            if (bex instanceof FunctionExpression)
            {
                validateFunction(c, (FunctionExpression) bex, knownTypes, c, usedVariables);
            }
        }
    }
    
    private void validateChainCompareExpression(ChainCompareExpression bex, TypeMapList knownTypes, PrototypeExpression pe, ClassExpression c, Map<String, String> usedVariables)
    {
        
        // TODO
            /*
        for (Expression e : bex.getExpressions())
        {
            if (!knownTypes.get(e.getReturnType()).hasOperator(bex.getOperator()))
            {
                System.err.println("unknown operator: " + bex.getOperator());
            }
        }
            */
        if (!knownTypes.get(c.getClassname()).hasOperator(bex.getOperator()))
        {
            System.err.println("unknown operator: " + bex.getOperator());
        }
    }
    
    private void validateReturnExpression(ReturnExpression bex, TypeMapList knownTypes, PrototypeExpression pe, ClassExpression c, Map<String, String> usedVariables)
    {
        validateBlock(new BlockExpression(List.of(bex.getReturnValue())), knownTypes, pe, c, usedVariables);
    }
    
    private void validateBinaryExpression(BinaryExpression b, TypeMapList knownTypes, PrototypeExpression pe, ClassExpression c, Map<String, String> usedVariables)
    {
        if (b.getLeft() instanceof VariableDefinitionExpression)
        {
            validateVariableDefinition(b.getLeft(), knownTypes, pe, c, usedVariables);
        }
        
        if (b.getLeft() instanceof BinaryExpression)
        {
            validateBinaryExpression((BinaryExpression) b.getLeft(), knownTypes, pe, c, usedVariables);
        }
        
        if (b.getLeft() instanceof ChainCompareExpression)
        {
            validateChainCompareExpression((ChainCompareExpression) b.getLeft(), knownTypes, pe, c, usedVariables);
        }
        
        if (b.getLeft() instanceof CallExpression)
        {
            validateCallExpression((CallExpression) b.getLeft(), knownTypes, pe, c, usedVariables);
        }
        
        if (b.getRight() instanceof BinaryExpression)
        {
            validateBinaryExpression((BinaryExpression) b.getRight(), knownTypes, pe, c, usedVariables);
        }
        
        if (b.getRight() instanceof ChainCompareExpression)
        {
            validateChainCompareExpression((ChainCompareExpression) b.getRight(), knownTypes, pe, c, usedVariables);
        }
        
        if (b.getRight() instanceof CallExpression)
        {
            validateCallExpression((CallExpression) b.getRight(), knownTypes, pe, c, usedVariables);
        }
        
        String className = "";
        
        if (b.getLeft() instanceof VariableExpression)
        {
            className = usedVariables.get(((VariableExpression) b.getLeft()).getName());
        }
        
        if (b.getLeft() instanceof VariableDefinitionExpression)
        {
            className = ((VariableDefinitionExpression) b.getLeft()).getType();
        }
        
        if (!className.isEmpty())
        {
            if (!knownTypes.get(className).hasOperator(b.getOp()))
            {
                System.err.format("unknown operator: %s in class %s%n", b.getOp(), className);
            }
        }
    }
    
    private void validateCallExpression(CallExpression exp, TypeMapList knownTypes, PrototypeExpression pe, ClassExpression c, Map<String, String> usedVariables)
    {
        List<String> parameterTypes = resolveToType(knownTypes, exp.getArguments());
        for (String type : parameterTypes)
        {
            if (!knownTypes.contains(type))
            {
                logSemanticError(String.format("Unknown type(%s) in method %s (in class %s)", type, pe.getName(), c.getClassname()));
            }
        }
        
        TypeMap classMap = knownTypes.get(c.getClassname());
        // TODO if the op of the parent binary is a . we need to join the left and right to find the correct callee
        
        
    }
    
    private List<String> resolveToType(TypeMapList knownTypes, List<Expression> arguments)
    {
        List<String> returnTypes = new ArrayList<>();
        
        for (Expression argument : arguments)
        {
            if (argument instanceof FloatExpression)
            {
                returnTypes.add("float");
                continue;
            }
            
            if (argument instanceof TextExpression)
            {
                returnTypes.add("text");
                continue;
            }
            
            if (argument instanceof CallExpression)
            {
                CallExpression ce = (CallExpression) argument;
                // TODO
            }
        }
        
        return returnTypes;
    }
    
    private void validateFunction(ClassExpression c, FunctionExpression be, TypeMapList types, ClassExpression classExpression, Map<String, String> typeMap)
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
            
            if (be.getBody().size() > 0 && !(be.getBody().get(0) instanceof NullExpression))
            {
                if (functionTypes.containsKey(par.getName()))
                {
                    logSemanticError(String.format("(%s %s) Parameter named \"%s\" is already defined in scope", c.getClassname(), be.getPrototype().getName(), par.getName()));
                }
            }
            
            if (!functionTypes.containsKey(par.getName()))
            {
                functionTypes.put(par.getName(), par.getType());
            }
        }
        
        if (!type.equals("Constructor") && !pe.getReturnType().equals("void"))
        {
            long count = be.getBody().stream().filter(e -> e instanceof ReturnExpression).count();
            if (count != 1)
            {
                logSemanticError(String.format("Expected one return statement in %s %s, got %s", type, pe.getName(), count));
            }
        }
        
        validateBlock(be.getBlock(), types, pe, c, functionTypes);
    }
    
    private TypeMapList buildTypeList(List<Expression> ast, TypeMapList types)
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
                ClassExpression c   = (ClassExpression) e;
                TypeMap         map = new TypeMapList.TypeMap(c.getClassname());
                
                for (Expression x : c.getBody())
                {
                    if (x instanceof FunctionExpression)
                    {
                        FunctionExpression fx = (FunctionExpression) x;
                        switch (fx.getVisibility())
                        {
                            case "global":
                            {
                                map.globals.add(fx.getPrototype());
                                continue;
                            }
                            case "function":
                            {
                                map.functions.add(fx.getPrototype());
                                continue;
                            }
                            case "operator":
                            {
                                map.operators.add(fx.getPrototype());
                                continue;
                            }
                            case "pure":
                            {
                                map.pures.add(fx.getPrototype());
                            }
                        }
                    }
                }
                types.add(map);
            }
        }
        
        for (ImportExpression e : imports)
        {
            if (!types.contains(e.getClassname()))
            {
                String     source     = Utils.readFileExternal(e.getLocation());
                SyntaxTree syntaxTree = new SyntaxTree(lexer.parse(e.getLocation(), source));
                buildTypeList(syntaxTree.getAST(), types);
            }
        }
        
        types.add(new TypeMapList.TypeMap("num"));
        return types;
    }
    
    private void logSemanticError(String s)
    {
        System.err.println(s);
    }
    
}
