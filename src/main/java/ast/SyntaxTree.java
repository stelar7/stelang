package ast;

import ast.exprs.*;
import ast.exprs.basic.*;
import ast.exprs.clazz.*;
import ast.exprs.control.*;
import ast.exprs.div.*;
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
        put(TokenType.UNKNOWN, 0);
        
        put(TokenType.DOT, 9);
        
        put(TokenType.PLUSPLUS, 10);
        put(TokenType.MINUSMINUS, 10);
        
        put(TokenType.PLUS, 20);
        put(TokenType.MINUS, 20);
        
        put(TokenType.ASTERISK, 30);
        put(TokenType.SLASH, 30);
        
        put(TokenType.SPACESHIP, 40);
        
        put(TokenType.RANGLE, 50);
        put(TokenType.LANGLE, 50);
        put(TokenType.LESSEQL, 50);
        put(TokenType.GREATEREQL, 50);
        
        put(TokenType.EQUAL, 60);
        put(TokenType.NOTEQL, 60);
        
        put(TokenType.AMPERSAND, 70);
        put(TokenType.CARET, 80);
        put(TokenType.BAR, 90);
        put(TokenType.AMPERSANDAMPERSAND, 100);
        put(TokenType.BARBAR, 110);
        
        put(TokenType.SET, 130);
        put(TokenType.SETEQL, 130);
        put(TokenType.SETNOTEQL, 130);
        put(TokenType.SETRANGLE, 130);
        put(TokenType.SETRANGLEEQL, 130);
        put(TokenType.SETLANGLE, 130);
        put(TokenType.SETLANGLEEQL, 130);
        put(TokenType.SETSPACESHIP, 130);
        put(TokenType.SETPLUS, 130);
        put(TokenType.SETMINUS, 130);
        put(TokenType.SETASTERIX, 130);
        put(TokenType.SETSLASH, 130);
        put(TokenType.SETPERCENT, 130);
        put(TokenType.SETANDAND, 130);
        put(TokenType.SETBARBAR, 130);
        put(TokenType.SETNOT, 130);
        put(TokenType.SETAND, 130);
        put(TokenType.SETBAR, 130);
        put(TokenType.SETCARET, 130);
        put(TokenType.SETRANGLERANGLE, 130);
        put(TokenType.SETLANGLELANGLE, 130);
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
    
    private Token peekPrevToken()
    {
        return tokens.get(tokenIndex - 2);
    }
    
    private boolean peekType(int i, TokenType othertype)
    {
        Token t = peekToken(i);
        return t.getType() == othertype;
    }
    
    private Token peekToken(int offset)
    {
        if (tokenIndex + offset > tokens.size())
        {
            return null;
        }
        
        return tokens.get(tokenIndex + (offset - 1));
    }
    
    private int getPrecedence()
    {
        return binOps.getOrDefault(currentToken.getType(), -1);
    }
    
    public boolean isValid()
    {
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
        nextToken();
        
        List<Expression> expressions = new ArrayList<>();
        while (currentToken.getType() != TokenType.RSQUIGLY)
        {
            expressions.add(parseExpression());
        }
        
        return expressions;
    }
    
    private Expression parseConstructorDeclaration()
    {
        String visibility = currentToken.getContent();
        nextToken();
        
        assertType(TokenType.IDENTIFIER);
        String identifier = currentToken.getContent();
        nextToken();
        
        PrototypeExpression f = parsePrototype(visibility, identifier);
        
        if (currentToken.getType() == TokenType.SEMICOLON)
        {
            nextToken();
            return new FunctionExpression(visibility, f, List.of());
        }
        
        assertType(TokenType.LSQUIGLY);
        nextToken();
        
        List<Expression> b = parseExpressionList();
        
        return new ConstructorExpression(visibility, f, b);
    }
    
    private Expression parseOperatorDeclaration()
    {
        nextToken();
        
        String identifier = currentToken.getContent();
        if (TokenType.from(identifier) == TokenType.UNKNOWN)
        {
            // TODO should this be allowed?
            logError("Unknown operator attempted overload");
        }
        
        nextToken();
        PrototypeExpression f = parsePrototype("operator", identifier);
        
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
        
        return new OperatorExpression("operator", f, b);
    }
    
    private Expression parseFunctionDeclaration()
    {
        String visibility = currentToken.getContent();
        nextToken();
        
        assertType(TokenType.IDENTIFIER);
        String identifier = currentToken.getContent();
        nextToken();
        
        PrototypeExpression f = parsePrototype(visibility, identifier);
        
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
                    if ((peekToken(1).getType() == TokenType.QUESTIONMARK) || (peekToken(1).getType() == TokenType.QUESTIONMARKCOLON))
                    {
                        return parseTernary();
                    }
                }
                
                if (allowVariableDeclaration)
                {
                    if (peekToken(1).getType() == TokenType.COLON)
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
            case TEXT:
            {
                return parseText();
            }
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
            case CONSTRUCTOR:
            {
                return parseConstructorDeclaration();
            }
            case OPERATOR:
            {
                return parseOperatorDeclaration();
            }
            default:
                return null;
        }
    }
    
    private Expression parseText()
    {
        String value = currentToken.getContent();
        nextToken();
        return new TextExpression(value);
    }
    
    private Expression parseMultilineText()
    {
        StringBuilder sb = new StringBuilder();
        
        while (!(peekType(0, TokenType.DOUBLEQUOTE) &&
                 peekType(1, TokenType.DOUBLEQUOTE) &&
                 peekType(2, TokenType.DOUBLEQUOTE)))
        {
            sb.append(currentToken.getContent());
            nextToken();
        }
        nextToken();
        nextToken();
        nextToken();
        return new TextExpression(sb.toString());
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
            
            return parseElse(condition, List.of(trueStatement));
            
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
            
            return parseElse(condition, trueStatements);
        }
    }
    
    private Expression parseElse(Expression condition, List<Expression> trueStatements)
    {
        if (currentToken.getType() != TokenType.ELSE)
        {
            return new IfExpression(condition, trueStatements, List.of());
        }
        nextToken();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression falseStatement = parseExpression();
            
            assertType(TokenType.SEMICOLON);
            nextToken();
            
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
    
    private Expression parseDo()
    {
        nextToken();
        
        List<Expression> body = new ArrayList<>();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression doStatement = parseExpression();
            
            assertType(TokenType.SEMICOLON);
            nextToken();
            
            body.add(doStatement);
        } else
        {
            nextToken();
            while (currentToken.getType() != TokenType.RSQUIGLY)
            {
                Expression parsed = parseExpression();
                body.add(parsed);
                
                assertType(TokenType.SEMICOLON);
                nextToken();
            }
            nextToken();
        }
        
        assertType(TokenType.WHILE);
        nextToken();
        
        assertType(TokenType.LPAREN);
        nextToken();
        
        Expression condition = parseExpression();
        
        assertType(TokenType.RPAREN);
        nextToken();
        
        return parseThenDo(condition, body);
    }
    
    private Expression parseThenDo(Expression condition, List<Expression> doStatements)
    {
        if (currentToken.getType() != TokenType.THEN)
        {
            if (currentToken.getType() == TokenType.SEMICOLON)
            {
                nextToken();
            }
            
            return new DoWhileExpression(condition, doStatements);
        }
        nextToken();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression thenStatements = parseExpression();
            
            assertType(TokenType.SEMICOLON);
            nextToken();
            
            return new DoWhileThenExpression(condition, doStatements, List.of(thenStatements));
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
            
            return new DoWhileThenExpression(condition, doStatements, falseStatements);
        }
    }
    
    private Expression parseWhile()
    {
        nextToken();
        
        assertType(TokenType.LPAREN);
        nextToken();
        
        Expression condition = parseExpression();
        
        assertType(TokenType.RPAREN);
        nextToken();
        
        if (currentToken.getType() == TokenType.SEMICOLON)
        {
            nextToken();
            return parseThenWhile(condition, List.of());
        }
        
        if (currentToken.getType() == TokenType.THEN)
        {
            return parseThenWhile(condition, List.of());
        }
        
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression doStatement = parseExpression();
            
            if (!(doStatement instanceof ControlExpression))
            {
                assertType(TokenType.SEMICOLON);
                nextToken();
            }
            
            return parseThenWhile(condition, List.of(doStatement));
        } else
        {
            nextToken();
            List<Expression> doStatements = new ArrayList<>();
            while (currentToken.getType() != TokenType.RSQUIGLY)
            {
                Expression parsed = parseExpression();
                doStatements.add(parsed);
                
                assertType(TokenType.SEMICOLON);
                nextToken();
            }
            nextToken();
            
            return parseThenWhile(condition, doStatements);
        }
    }
    
    private Expression parseThenWhile(Expression condition, List<Expression> doStatements)
    {
        if (currentToken.getType() != TokenType.THEN)
        {
            return new WhileExpression(condition, doStatements);
        }
        nextToken();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression thenStatements = parseExpression();
            
            assertType(TokenType.SEMICOLON);
            nextToken();
            
            return new WhileThenExpression(condition, doStatements, List.of(thenStatements));
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
            
            return new WhileThenExpression(condition, doStatements, falseStatements);
        }
    }
    
    
    private Expression parseFor()
    {
        nextToken();
        
        assertType(TokenType.LPAREN);
        nextToken();
        
        List<Expression> init = parseCommaSeparatedExpressions(TokenType.SEMICOLON, TokenType.COLON);
        
        if (peekPrevToken().getType() == TokenType.COLON)
        {
            return parseForEach(init);
        }
        
        List<Expression> condition = parseCommaSeparatedExpressions(TokenType.SEMICOLON);
        List<Expression> increment = parseCommaSeparatedExpressions(TokenType.RPAREN);
        
        if (currentToken.getType() == TokenType.SEMICOLON)
        {
            nextToken();
            return parseThenFor(init, condition, increment, List.of());
        }
        
        if (currentToken.getType() == TokenType.THEN)
        {
            return parseThenFor(init, condition, increment, List.of());
        }
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression doStatement = parseExpression();
            
            if (!(doStatement instanceof ControlExpression))
            {
                assertType(TokenType.SEMICOLON);
                nextToken();
            }
            
            return parseThenFor(init, condition, increment, List.of(doStatement));
        } else
        {
            nextToken();
            List<Expression> doStatements = new ArrayList<>();
            while (currentToken.getType() != TokenType.RSQUIGLY)
            {
                Expression parsed = parseExpression();
                doStatements.add(parsed);
                
                assertType(TokenType.SEMICOLON);
                nextToken();
            }
            nextToken();
            
            return parseThenFor(init, condition, increment, doStatements);
        }
    }
    
    private Expression parseForEach(List<Expression> init)
    {
        Expression collection = parseExpression();
        assertType(TokenType.RPAREN);
        nextToken();
        
        if (currentToken.getType() == TokenType.SEMICOLON)
        {
            nextToken();
            return parseThenForEach(init, collection, List.of());
        }
        
        if (currentToken.getType() == TokenType.THEN)
        {
            return parseThenForEach(init, collection, List.of());
        }
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression doStatement = parseExpression();
            
            if (!(doStatement instanceof ControlExpression))
            {
                assertType(TokenType.SEMICOLON);
                nextToken();
            }
            
            return parseThenForEach(init, collection, List.of(doStatement));
        } else
        {
            nextToken();
            List<Expression> doStatements = new ArrayList<>();
            while (currentToken.getType() != TokenType.RSQUIGLY)
            {
                Expression parsed = parseExpression();
                doStatements.add(parsed);
                
                assertType(TokenType.SEMICOLON);
                nextToken();
            }
            nextToken();
            
            return parseThenForEach(init, collection, doStatements);
        }
    }
    
    private Expression parseThenForEach(List<Expression> init, Expression collection, List<Expression> doStatements)
    {
        if (currentToken.getType() != TokenType.THEN)
        {
            return new ForEachExpression(init, collection, doStatements);
        }
        nextToken();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression thenStatements = parseExpression();
            
            assertType(TokenType.SEMICOLON);
            nextToken();
            
            return new ForEachThenExpression(init, collection, doStatements, List.of(thenStatements));
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
            
            return new ForEachThenExpression(init, collection, doStatements, falseStatements);
        }
    }
    
    private List<Expression> parseCommaSeparatedExpressions(TokenType... endTokens)
    {
        List.of(endTokens);
        List<Expression> data = new ArrayList<>();
        
        while (!List.of(endTokens).contains(currentToken.getType()))
        {
            Expression parsed = parseExpression();
            data.add(parsed);
            
            if (List.of(endTokens).contains(currentToken.getType()))
            {
                nextToken();
                return data;
            }
            
            if (currentToken.getType() != TokenType.COMMA)
            {
                logError("Expected , in argument list");
            } else
            {
                nextToken();
            }
        }
        nextToken();
        return data;
    }
    
    private Expression parseThenFor(List<Expression> init, List<Expression> condition, List<Expression> increment, List<Expression> doStatements)
    {
        if (currentToken.getType() != TokenType.THEN)
        {
            return new ForExpression(init, condition, increment, doStatements);
        }
        nextToken();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression thenStatements = parseExpression();
            
            assertType(TokenType.SEMICOLON);
            nextToken();
            
            return new ForThenExpression(init, condition, increment, doStatements, List.of(thenStatements));
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
            
            return new ForThenExpression(init, condition, increment, doStatements, falseStatements);
        }
    }
    
    
    private Expression parseEnum()
    {
        nextToken();
        
        assertType(TokenType.IDENTIFIER);
        String identifier = currentToken.getContent();
        nextToken();
        
        assertType(TokenType.LSQUIGLY);
        nextToken();
        
        List<Expression> members = parseEnumMembers();
        
        if (currentToken.getType() == TokenType.RSQUIGLY)
        {
            nextToken();
            return new EnumExpression(identifier, members, List.of());
        }
        
        List<Expression> methods = parseExpressionList();
        
        return new EnumExpression(identifier, members, methods);
    }
    
    private List<Expression> parseEnumMembers()
    {
        List<Expression> members = new ArrayList<>();
        while (currentToken.getType() != TokenType.SEMICOLON)
        {
            assertType(TokenType.IDENTIFIER);
            String name = currentToken.getContent();
            nextToken();
            
            if (currentToken.getType() == TokenType.LPAREN)
            {
                nextToken();
                List<Expression> params = new ArrayList<>();
                while (currentToken.getType() != TokenType.RPAREN)
                {
                    Expression parsed = parseExpression();
                    params.add(parsed);
                    
                    if (currentToken.getType() == TokenType.RPAREN)
                    {
                        break;
                    }
                    
                    assertType(TokenType.COMMA);
                    nextToken();
                }
                nextToken();
                members.add(new EnumMemberExpression(name, params));
                if (currentToken.getType() == TokenType.COMMA)
                {
                    nextToken();
                    continue;
                }
            }
            
            if (currentToken.getType() == TokenType.COMMA)
            {
                members.add(new EnumMemberExpression(name, List.of()));
                nextToken();
            }
        }
        
        nextToken();
        return members;
    }
    
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
                
                if (currentToken.getType() == TokenType.RPAREN)
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
    
    private PrototypeExpression parsePrototype(String visibility, String identifier)
    {
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
        
        if (visibility.equals("constructor"))
        {
            if (currentToken.getType() == TokenType.SEMICOLON)
            {
                return new PrototypeExpression(identifier, params, identifier);
            }
        }
        
        assertType(TokenType.COLON);
        nextToken();
        
        assertType(TokenType.IDENTIFIER);
        String returnType = currentToken.getContent();
        nextToken();
        
        
        return new PrototypeExpression(identifier, params, returnType);
    }
    
    private Expression parseImport()
    {
        nextToken();
        
        assertType(TokenType.IDENTIFIER);
        String classname = currentToken.getContent();
        nextToken();
        
        assertType(TokenType.FROM);
        nextToken();
        
        
        assertType(TokenType.TEXT);
        String filename = currentToken.getContent();
        nextToken();
        
        assertType(TokenType.SEMICOLON);
        nextToken();
        
        return new ImportExpression(classname, filename);
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
