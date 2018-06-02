package ast;

import ast.exprs.*;
import com.google.gson.Gson;
import lexer.*;

import java.util.*;

public class SyntaxTree
{
    private List<Token> tokens;
    private int         tokenIndex;
    
    private Token currentToken;
    
    public SyntaxTree(List<Token> tokens)
    {
        this.tokens = tokens;
    }
    
    private static Map<TokenType, Integer> binOps = new HashMap<>()
    {{
        put(TokenType.UNKNOWN, 1);
        
        put(TokenType.PLUS, 2);
        put(TokenType.MINUS, 2);
        
        put(TokenType.ASTERISK, 3);
        put(TokenType.SLASH, 3);
        
        put(TokenType.SPACESHIP, 4);
        
        put(TokenType.RANGLE, 5);
        put(TokenType.LANGLE, 5);
        put(TokenType.LESSEQL, 5);
        put(TokenType.GREATEREQL, 5);
        
        put(TokenType.EQUAL, 6);
        put(TokenType.NOTEQL, 6);
        
        put(TokenType.AMPERSAND, 7);
        put(TokenType.CARET, 8);
        put(TokenType.BAR, 9);
        put(TokenType.AMPERSANDAMPERSAND, 10);
        put(TokenType.BARBAR, 11);
        
        put(TokenType.QUESTIONMARK, 12);
        
        put(TokenType.SET, 13);
        put(TokenType.SETEQL, 13);
        put(TokenType.SETNOTEQL, 13);
        put(TokenType.SETRANGLE, 13);
        put(TokenType.SETRANGLEEQL, 13);
        put(TokenType.SETLANGLE, 13);
        put(TokenType.SETLANGLEEQL, 13);
        put(TokenType.SETSPACESHIP, 13);
        put(TokenType.SETPLUS, 13);
        put(TokenType.SETMINUS, 13);
        put(TokenType.SETASTERIX, 13);
        put(TokenType.SETSLASH, 13);
        put(TokenType.SETPERCENT, 13);
        put(TokenType.SETANDAND, 13);
        put(TokenType.SETBARBAR, 13);
        put(TokenType.SETNOT, 13);
        put(TokenType.SETAND, 13);
        put(TokenType.SETBAR, 13);
        put(TokenType.SETCARET, 13);
        put(TokenType.SETRANGLERANGLE, 13);
        put(TokenType.SETLANGLELANGLE, 13);
    }};
    
    private void nextToken()
    {
        if (tokenIndex + 1 > tokens.size())
        {
            currentToken = null;
            return;
        }
        
        currentToken = tokens.get(tokenIndex++);
    }
    
    private Token peekToken()
    {
        if (tokenIndex + 1 > tokens.size())
        {
            return null;
        }
        
        return tokens.get(tokenIndex + 1);
    }
    
    private int getPrecedence()
    {
        return binOps.getOrDefault(currentToken.getType(), -1);
    }
    
    public boolean isValid()
    {
        // first token
        nextToken();
        
        Syntax s;
        switch (currentToken.getType())
        {
            case IMPORT:
            {
                s = parseImport();
                break;
            }
            case CLASS:
            {
                s = parseClass();
                break;
            }
            case ENUM:
            {
                s = parseEnum();
                break;
            }
            default:
            {
                logError("Invalid start of file, must start with import or class definition.");
                s = null;
            }
        }
        
        System.out.println(new Gson().toJson(s));
        return true;
    }
    
    private Syntax parseClass()
    {
        // class x {
        // eat class token
        nextToken();
        
        assertType(TokenType.IDENTIFIER);
        String classname = currentToken.getContent();
        nextToken();
        
        assertType(TokenType.LSQUIGLY);
        
        List<Syntax> body = handleClassBody();
        
        assertType(TokenType.RSQUIGLY);
        nextToken();
        
        return new ClassSyntax(classname, body);
    }
    
    private List<Syntax> handleClassBody()
    {
        // {
        nextToken();
        
        List<Syntax> syntaxes = new ArrayList<>();
        while (currentToken.getType() != TokenType.RSQUIGLY)
        {
            switch (currentToken.getType())
            {
                // class member field
                case CONST:
                case VAL:
                case IDENTIFIER:
                {
                    String visibility = currentToken.getContent();
                    nextToken();
                    
                    assertType(TokenType.IDENTIFIER);
                    String identifier = currentToken.getContent();
                    nextToken();
                    
                    Expression value = new NullExpression();
                    if (currentToken.getType() == TokenType.SEMICOLON)
                    {
                        syntaxes.add(new VariableDefinitionSyntax(identifier, visibility, value));
                        break;
                    }
                    
                    if (TokenType.isSetType(currentToken))
                    {
                        nextToken();
                        value = parseExpression();
                    }
                    
                    assertType(TokenType.SEMICOLON);
                    nextToken();
                    
                    syntaxes.add(new VariableDefinitionSyntax(identifier, visibility, value));
                    break;
                }
                
                // class functions
                case PURE:
                case GLOBAL:
                case FUNCTION:
                {
                    // pure add(a:a, a:b):a {return a + b;}
                    
                    // pure
                    String visibility = currentToken.getContent();
                    nextToken();
                    
                    //add(a:a, a:b):a
                    assertType(TokenType.IDENTIFIER);
                    String identifier = currentToken.getContent();
                    nextToken();
                    
                    PrototypeSyntax f = parsePrototype(identifier);
                    
                    assertType(TokenType.LSQUIGLY);
                    nextToken();
                    
                    // return a + b;
                    List<Expression> b = new ArrayList<>();
                    while (currentToken.getType() != TokenType.RSQUIGLY)
                    {
                        Expression e = parseExpression();
                        nextToken();
                        
                        b.add(e);
                    }
                    
                    assertType(TokenType.RSQUIGLY);
                    nextToken();
                    
                    syntaxes.add(new FunctionSyntax(visibility, f, b));
                    break;
                }
                
                case OPERATOR:
                {
                    //operator+ (a:a, b: a): a {
                    //    return a.some_field + b.other_field;
                    //}
                    
                    // operator
                    nextToken();
                    
                    String identifier = currentToken.getContent();
                    if (TokenType.from(identifier) == TokenType.UNKNOWN)
                    {
                        // TODO should this be allowed?
                        logError("Unknown operator attempted overload");
                    }
                    
                    nextToken();
                    PrototypeSyntax f = parsePrototype(identifier);
                    
                    assertType(TokenType.LSQUIGLY);
                    nextToken();
                    
                    List<Expression> b = new ArrayList<>();
                    while (currentToken.getType() != TokenType.RSQUIGLY)
                    {
                        Expression e = parseExpression();
                        nextToken();
                        
                        b.add(e);
                    }
                    
                    assertType(TokenType.RSQUIGLY);
                    nextToken();
                    
                    syntaxes.add(new FunctionSyntax("operator", f, b));
                    break;
                }
            }
        }
        
        return syntaxes;
    }
    
    private Expression parsePrimary()
    {
        switch (currentToken.getType())
        {
            case SWITCH:
                return parseSwitch();
            case IF:
                return parseIf();
            case FOR:
                return parseFor();
            case WHILE:
                return parseWhile();
            case DO:
                return parseDo();
            case IDENTIFIER:
                return parseIdentifier();
            case DOTDOT:
                return parseDotdot();
            case NUMBER:
                return parseNumber();
            case LPAREN:
                return parseParenthesis();
            case RETURN:
            {
                nextToken();
                return parseExpression();
            }
            default:
                return null;
        }
    }
    
    private Expression parseDotdot()
    {
        String[] c = currentToken.getContent().split("\\.\\.");
        long     f = Long.parseUnsignedLong(c[0]);
        long     s = Long.parseUnsignedLong(c[1]);
        
        nextToken();
        
        List<Long> params = new ArrayList<>();
        for (; f <= s; f++)
        {
            params.add(f);
        }
        
        return new ArrayExpression(params);
    }
    
    private Expression parseNumber()
    {
        double num = Double.parseDouble(currentToken.getContent());
        nextToken();
        return new NumberExpression(num);
    }
    
    // todo start
    
    private Expression parseDo()
    {
        return null;
    }
    
    private Expression parseWhile()
    {
        return null;
    }
    
    private Expression parseFor()
    {
        return null;
    }
    
    private Syntax parseEnum()
    {
        return null;
    }
    
    private Expression parseBinaryOps(int i, Expression left)
    {
        return left;
    }
    
    private Expression parseParenthesis()
    {
        return null;
    }
    
    private Expression parseIf()
    {
        return null;
    }
    
    // TODO end
    
    
    private Expression parseIdentifier()
    {
        String id = currentToken.getContent();
        nextToken();
        
        if (currentToken.getType() != TokenType.LPAREN)
        {
            return new VariableExpression(id);
        }
        
        nextToken();
        List<Expression> parameters = new ArrayList<>();
        if (currentToken.getType() != TokenType.RPAREN)
        {
            while (true)
            {
                Expression e = parseExpression();
                parameters.add(e);
                
                if (currentToken.getType() != TokenType.RPAREN)
                {
                    break;
                }
                
                if (currentToken.getType() != TokenType.COMMA)
                {
                    logError("Expected , or ) in argument list");
                }
                
                nextToken();
            }
        }
        
        nextToken();
        
        return new CallExpression(id, parameters);
    }
    
    private Expression parseSwitch()
    {
        // switch
        nextToken();
        
        assertType(TokenType.LPAREN);
        nextToken();
        
        Expression e = parseExpression();
        
        assertType(TokenType.RPAREN);
        nextToken();
        
        assertType(TokenType.LSQUIGLY);
        nextToken();
        
        List<SwitchParameter> cases = new ArrayList<>();
        
        int             caseIndex    = -1;
        Expression      condition    = new NullExpression();
        Expression      expression   = new NullExpression();
        SwitchParameter defaultParam = new SwitchParameter(caseIndex, condition, expression);
        
        while (currentToken.getType() != TokenType.RSQUIGLY)
        {
            if (currentToken.getType() == TokenType.CASE)
            {
                caseIndex++;
                
                nextToken();
                Expression exp = parseExpression();
                
                assertType(TokenType.COLON);
                nextToken();
                
                assertType(TokenType.LSQUIGLY);
                nextToken();
                
                Expression body = parseExpression();
                
                assertType(TokenType.RSQUIGLY);
                nextToken();
                
                assertType(TokenType.SEMICOLON);
                nextToken();
                cases.add(new SwitchParameter(caseIndex, exp, body));
            }
            
            if (currentToken.getType() == TokenType.DEFAULT)
            {
                nextToken();
                
                assertType(TokenType.COLON);
                nextToken();
                
                assertType(TokenType.LSQUIGLY);
                nextToken();
                
                Expression body = parseExpression();
                
                assertType(TokenType.RSQUIGLY);
                nextToken();
                
                assertType(TokenType.SEMICOLON);
                nextToken();
                
                defaultParam = new SwitchParameter(-1, condition, body);
            }
        }
        
        nextToken();
        return new SwitchExpression(cases, defaultParam);
    }
    
    private Expression parseExpression()
    {
        Expression left = parsePrimary();
        
        return parseBinaryOps(0, left);
    }
    
    private PrototypeSyntax parsePrototype(String identifier)
    {
        // add(a:b, a:c):a
        
        assertType(TokenType.LPAREN);
        
        List<PrototypeParameter> params = new ArrayList<>();
        do
        {
            nextToken();
            if (currentToken.getType() != TokenType.RPAREN)
            {
                assertType(TokenType.IDENTIFIER);
                String clazz = currentToken.getContent();
                nextToken();
                
                assertType(TokenType.COLON);
                nextToken();
                
                assertType(TokenType.IDENTIFIER);
                String name = currentToken.getContent();
                nextToken();
                
                params.add(new PrototypeParameter(clazz, name));
            }
        } while (currentToken.getType() == TokenType.COMMA);
        nextToken();
        
        assertType(TokenType.COLON);
        nextToken();
        
        assertType(TokenType.IDENTIFIER);
        String returnType = currentToken.getContent();
        nextToken();
        
        
        return new PrototypeSyntax(identifier, params, returnType);
    }
    
    private Syntax parseImport()
    {
        // import x from y;
        
        // import keyword
        nextToken();
        
        assertType(TokenType.IDENTIFIER);
        String classname = currentToken.getContent();
        nextToken();
        
        assertType(TokenType.FROM);
        nextToken();
        
        assertType(TokenType.IDENTIFIER);
        String location = currentToken.getContent();
        nextToken();
        
        assertType(TokenType.SEMICOLON);
        nextToken();
        
        return new ImportSyntax(classname, location);
    }
    
    private void assertType(TokenType identifier)
    {
        if (currentToken.getType() == identifier)
        {
            return;
        }
        
        System.err.print("Expected token " + identifier + ", Current: " + currentToken);
        System.exit(0);
    }
    
    private void logError(String s)
    {
        System.err.println(s);
        System.err.print("Current token is: ");
        System.err.println(currentToken);
    }
}
