package ast;

import ast.exprs.*;
import ast.exprs.basic.*;
import ast.exprs.control.*;
import com.google.gson.*;
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
        
        return tokens.get(tokenIndex);
    }
    
    private int getPrecedence()
    {
        return binOps.getOrDefault(currentToken.getType(), -1);
    }
    
    public boolean isValid()
    {
        // first token
        nextToken();
        
        List<Expression> s = new ArrayList<>();
        while (currentToken.getType() != TokenType.UNKNOWN)
        {
            switch (currentToken.getType())
            {
                case IMPORT:
                {
                    s.add(parseImport());
                    break;
                }
                case CLASS:
                {
                    s.add(parseClass());
                    break;
                }
                case ENUM:
                {
                    s.add(parseEnum());
                    break;
                }
                default:
                {
                    logError("Invalid start of file, must start with import or class definition.");
                }
            }
        }
        
        System.out.println(new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(s));
        return true;
    }
    
    private Expression parseClass()
    {
        // class x {
        // eat class token
        nextToken();
        
        assertType(TokenType.IDENTIFIER);
        String classname = currentToken.getContent();
        nextToken();
        
        assertType(TokenType.LSQUIGLY);
        nextToken();
        
        List<Expression> body = parseExpressionList();
        
        return new ClassExpression(classname, body);
    }
    
    private List<Expression> handleClassBody()
    {
        // {
        nextToken();
        
        List<Expression> expressions = new ArrayList<>();
        while (currentToken.getType() != TokenType.RSQUIGLY)
        {
            expressions.add(parseExpression());
        }
        
        return expressions;
    }
    
    private Expression parseOperatorDeclaration()
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
        PrototypeExpression f = parsePrototype(identifier);
        
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
        
        return new FunctionExpression("operator", f, b);
    }
    
    private Expression parseFunctionDeclaration()
    {
        // pure add(a:a, a:b):a {return a + b;}
        
        // pure
        String visibility = currentToken.getContent();
        nextToken();
        
        //add(a:a, a:b):a
        assertType(TokenType.IDENTIFIER);
        String identifier = currentToken.getContent();
        nextToken();
        
        PrototypeExpression f = parsePrototype(identifier);
        
        assertType(TokenType.LSQUIGLY);
        nextToken();
        
        List<Expression> b = parseExpressionList();
        
        return new FunctionExpression(visibility, f, b);
    }
    
    private List<Expression> parseExpressionList()
    {
        List<Expression> b = new ArrayList<>();
        while (currentToken.getType() != TokenType.RSQUIGLY)
        {
            Expression e = parseExpression();
            b.add(e);
            
            if (!(e instanceof ControlExpression))
            {
                assertType(TokenType.SEMICOLON);
                nextToken();
            }
        }
        
        nextToken();
        return b;
    }
    
    private Expression parseVariableDefinition()
    {
        String    visibility = currentToken.getContent();
        TokenType vType      = currentToken.getType();
        nextToken();
        
        if (vType != TokenType.VAL && vType != TokenType.CONST)
        {
            assertType(TokenType.COLON);
            nextToken();
        }
        
        assertType(TokenType.IDENTIFIER);
        String identifier = currentToken.getContent();
        nextToken();
        
        return new VariableDefinitionExpression(identifier, visibility);
    }
    
    private Expression parsePrimary(boolean allowTernary, boolean allowVariableDeclaration)
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
            {
                if (allowTernary)
                {
                    if ((peekToken().getType() == TokenType.QUESTIONMARK) || (peekToken().getType() == TokenType.QUESTIONMARKCOLON))
                    {
                        return parseTernary();
                    }
                }
                
                if (allowVariableDeclaration)
                {
                    if (peekToken().getType() == TokenType.COLON)
                    {
                        return parseVariableDefinition();
                    }
                }
                
                return parseIdentifier();
            }
            case DOTDOT:
                return parseDotdot();
            case NUMBER:
                return parseNumber();
            case LPAREN:
                return parseParenthesis();
            case ASSERT:
                return parseAssert();
            case RETURN:
            {
                nextToken();
                Expression parsed = parseExpression();
                Expression value  = new ReturnExpression(parsed);
                return value;
            }
            case CONST:
            case VAL:
            {
                return parseVariableDefinition();
            }
            case PURE:
            case GLOBAL:
            case FUNCTION:
            {
                return parseFunctionDeclaration();
            }
            case OPERATOR:
            {
                return parseOperatorDeclaration();
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
        
        List<Object> params = new ArrayList<>();
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
    
    private Expression parseTernary()
    {
        Expression condition = parseExpression(false, false);
        
        if (currentToken.getType() == TokenType.QUESTIONMARK)
        {
            nextToken();
            Expression trueCond = parseExpression(true, false);
            assertType(TokenType.COLON);
            nextToken();
            Expression falseCond = parseExpression(true, false);
            
            return new TernaryExpression(condition, trueCond, falseCond);
        }
        
        if (currentToken.getType() == TokenType.QUESTIONMARKCOLON)
        {
            nextToken();
            Expression falseCond = parseExpression();
            return new TernaryExpression(condition, condition, falseCond);
        }
        
        logError("Invalid ternary syntax, expected ? or ?:");
        return null;
    }
    
    private Expression parseIf()
    {
        // if
        nextToken();
        
        assertType(TokenType.LPAREN);
        nextToken();
        
        Expression condition = parseExpression();
        
        assertType(TokenType.RPAREN);
        nextToken();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression trueStatement = parseExpression();
            
            assertType(TokenType.SEMICOLON);
            nextToken();
            
            if (currentToken.getType() == TokenType.ELSE)
            {
                nextToken();
                return parseElse(condition, List.of(trueStatement));
            }
            
            return new IfExpression(condition, List.of(trueStatement), List.of(new NullExpression()));
        } else
        {
            nextToken();
            List<Expression> trueStatements = new ArrayList<>();
            while (currentToken.getType() != TokenType.RSQUIGLY)
            {
                Expression parsed = parseExpression();
                trueStatements.add(parsed);
                
                assertType(TokenType.SEMICOLON);
                nextToken();
            }
            nextToken();
            
            if (currentToken.getType() == TokenType.ELSE)
            {
                nextToken();
                return parseElse(condition, trueStatements);
            }
            
            return new IfExpression(condition, trueStatements, List.of(new NullExpression()));
        }
    }
    
    private Expression parseElse(Expression condition, List<Expression> trueStatements)
    {
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression falseStatement = parseExpression();
            return new IfExpression(condition, trueStatements, List.of(falseStatement));
        } else
        {
            nextToken();
            List<Expression> falseStatements = new ArrayList<>();
            while (currentToken.getType() != TokenType.RSQUIGLY)
            {
                Expression parsed = parseExpression();
                falseStatements.add(parsed);
                
                assertType(TokenType.SEMICOLON);
                nextToken();
            }
            nextToken();
            
            return new IfExpression(condition, trueStatements, falseStatements);
        }
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
    
    private Expression parseEnum()
    {
        return null;
    }
    
    // TODO end
    
    private Expression parseAssert()
    {
        nextToken();
        Expression condition = parseExpression();
        
        return new AssertExpression(condition);
    }
    
    private Expression parseBinaryOps(int exprPre, Expression left)
    {
        while (true)
        {
            int precedence = getPrecedence();
            if (precedence < exprPre)
            {
                return left;
            }
            
            Token op = currentToken;
            nextToken();
            
            Expression right = parseExpression();
            
            int next = getPrecedence();
            if (precedence < next)
            {
                right = parseBinaryOps(precedence + 1, right);
            }
            
            left = new BinaryExpression(op, left, right);
        }
    }
    
    private Expression parseParenthesis()
    {
        nextToken();
        Expression v = parseExpression();
        assertType(TokenType.RPAREN);
        nextToken();
        return v;
    }
    
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
        SwitchParameter defaultParam = new SwitchParameter(caseIndex, condition, List.of(expression));
        
        while (currentToken.getType() != TokenType.RSQUIGLY)
        {
            if (currentToken.getType() == TokenType.CASE)
            {
                caseIndex++;
                
                nextToken();
                Expression exp;
                
                if (currentToken.getType() == TokenType.LSQUIGLY)
                {
                    nextToken();
                    List<Object> exps = new ArrayList<>();
                    while (currentToken.getType() != TokenType.RSQUIGLY)
                    {
                        Expression parsed = parseExpression();
                        if (currentToken.getType() == TokenType.RSQUIGLY)
                        {
                            break;
                        }
                        
                        assertType(TokenType.COMMA);
                        nextToken();
                    }
                    
                    nextToken();
                    exp = new ArrayExpression(exps);
                } else
                {
                    exp = parseExpression();
                }
                
                assertType(TokenType.COLON);
                nextToken();
                
                if (currentToken.getType() != TokenType.LSQUIGLY)
                {
                    assertType(TokenType.RETURN);
                    Expression body = parseExpression();
                    cases.add(new SwitchParameter(caseIndex, exp, List.of(body)));
                    
                    assertType(TokenType.SEMICOLON);
                    nextToken();
                    
                    continue;
                }
                
                nextToken();
                
                List<Expression> body = parseExpressionList();
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
                
                defaultParam = new SwitchParameter(-1, condition, List.of(body));
            }
        }
        
        nextToken();
        
        return new SwitchExpression(cases, defaultParam);
    }
    
    private Expression parseExpression(boolean ternary, boolean allowVarDec)
    {
        Expression left = parsePrimary(ternary, allowVarDec);
        return parseExpressionImpl(left);
    }
    
    
    private Expression parseExpression()
    {
        Expression left = parsePrimary(true, true);
        return parseExpressionImpl(left);
    }
    
    private Expression parseExpressionImpl(Expression left)
    {
        Expression bin = parseBinaryOps(0, left);
        return bin;
    }
    
    private PrototypeExpression parsePrototype(String identifier)
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
        
        
        return new PrototypeExpression(identifier, params, returnType);
    }
    
    private Expression parseImport()
    {
        // import x from y;
        
        // import keyword
        nextToken();
        
        assertType(TokenType.IDENTIFIER);
        String classname = currentToken.getContent();
        nextToken();
        
        assertType(TokenType.FROM);
        nextToken();
        
        
        assertType(TokenType.DOUBLEQUOTE);
        nextToken();
        
        
        StringBuilder filename = new StringBuilder();
        while (currentToken.getType() != TokenType.DOUBLEQUOTE)
        {
            String location = currentToken.getContent();
            filename.append(location);
            nextToken();
        }
        
        assertType(TokenType.DOUBLEQUOTE);
        nextToken();
        
        assertType(TokenType.SEMICOLON);
        nextToken();
        
        return new ImportExpression(classname, filename.toString());
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
