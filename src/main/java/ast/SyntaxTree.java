package ast;

import ast.exprs.*;
import ast.exprs.basic.*;
import ast.exprs.clazz.*;
import ast.exprs.control.*;
import ast.exprs.div.*;
import lexer.*;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class SyntaxTree
{
    private List<Token> tokens;
    private int         tokenIndex;
    
    private Token currentToken;
    
    private List<Expression> ast;
    
    public List<Expression> getAST()
    {
        return ast;
    }
    
    public SyntaxTree(List<Token> tokens)
    {
        this.tokens = tokens.stream().filter(t -> t.getType() != TokenType.COMMENT).collect(Collectors.toList());
        nextToken();
        
        ast = validateSyntax();
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
        put(TokenType.PERCENT, 50);
        put(TokenType.LESSEQL, 50);
        put(TokenType.GREATEREQL, 50);
        
        put(TokenType.EQUAL, 60);
        put(TokenType.NOTEQL, 60);
        
        put(TokenType.AMPERSAND, 70);
        put(TokenType.RSHIFT, 70);
        put(TokenType.LSHIFT, 70);
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
    
    private void logParseError(String s)
    {
        System.err.println(s);
        System.err.print("Current token is: ");
        System.err.println(currentToken);
    }
    
    private void logParseError(String s, Expression e)
    {
        System.err.println(s);
        System.err.print("Current expression is: ");
        System.err.println(e);
    }
    
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
    
    private TokenType assertType(TokenType... identifier)
    {
        for (TokenType type : identifier)
        {
            if (currentToken.getType() == type)
            {
                return type;
            }
        }
        
        System.err.print("Expected token " + Arrays.toString(identifier) + ", Current: " + currentToken);
        System.exit(0);
        return TokenType.UNKNOWN;
    }
    
    private TokenType nextTokenSkippingWhitespace()
    {
        do
        {
            nextToken();
        } while (currentToken.getType() == TokenType.WHITESPACE);
        
        return currentToken.getType();
    }
    
    private TokenType assertThenNext(TokenType... type)
    {
        TokenType returnType = assertType(type);
        do
        {
            nextToken();
            if (currentToken == null)
            {
                break;
            }
        } while (currentToken.getType() == TokenType.WHITESPACE);
        
        return returnType;
    }
    
    private String assertGetThenNext(TokenType... type)
    {
        assertType(type);
        String content = currentToken.getContent();
        do
        {
            nextToken();
        } while (currentToken.getType() == TokenType.WHITESPACE);
        
        return content;
    }
    
    private int getPrecedence()
    {
        return binOps.getOrDefault(currentToken.getType(), -1);
    }
    
    public List<Expression> validateSyntax()
    {
        List<Expression> s = new ArrayList<>();
        while (currentToken != null)
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
                    ClassExpression c = (ClassExpression) parseClass();
                    for (Expression ex : new ArrayList<>(c.getBody()))
                    {
                        if (ex instanceof MultiExpression)
                        {
                            c.getBody().remove(ex);
                            MultiExpression m = (MultiExpression) ex;
                            c.getBody().addAll(m.getList());
                        }
                    }
                    
                    s.add(c);
                    break;
                }
                case ENUM:
                {
                    s.add(parseEnum());
                    break;
                }
                default:
                {
                    logParseError("Invalid start of file, must start with import or class definition.");
                }
            }
        }
        
        return s;
    }
    
    private Expression parseClass()
    {
        assertThenNext(TokenType.CLASS);
        String classname  = assertGetThenNext(TokenType.IDENTIFIER);
        String superClass = "object";
        
        if (currentToken.getType() == TokenType.EXTENDS)
        {
            assertThenNext(TokenType.EXTENDS);
            superClass = assertGetThenNext(TokenType.IDENTIFIER);
        }
        
        ClassBlockExpression body = parseClassBlockExpression();
        return new ClassExpression(classname, body, superClass);
    }
    
    private List<VariableExpression> parseGenericsExpression()
    {
        List<VariableExpression> list = new ArrayList<>();
        
        assertThenNext(TokenType.LANGLE);
        while (currentToken.getType() != TokenType.RANGLE)
        {
            list.add((VariableExpression) parseIdentifier());
            if (currentToken.getType() != TokenType.COMMA)
            {
                break;
            }
            nextTokenSkippingWhitespace();
        }
        nextTokenSkippingWhitespace();
        
        return list;
    }
    
    private ClassBlockExpression parseClassBlockExpression()
    {
        assertThenNext(TokenType.LSQUIGLY);
        List<Expression> body = parseClassExpressionList();
        assertThenNext(TokenType.RSQUIGLY);
        return new ClassBlockExpression(body);
    }
    
    private List<Expression> parseClassExpressionList()
    {
        List<Expression> b = new ArrayList<>();
        while (currentToken.getType() != TokenType.RSQUIGLY)
        {
            Expression e = parseExpression();
            
            if (e instanceof BinaryExpression)
            {
                if (!TokenType.isSetType(((BinaryExpression) e).getOp()))
                {
                    logParseError("Binary expressions are not allowed here");
                }
            }
            
            b.add(e);
            if (!(e instanceof ControlExpression))
            {
                assertThenNext(TokenType.SEMICOLON);
            }
        }
        return b;
    }
    
    
    private List<Expression> handleClassBody()
    {
        nextTokenSkippingWhitespace();
        
        List<Expression> expressions = new ArrayList<>();
        while (currentToken.getType() != TokenType.RSQUIGLY)
        {
            expressions.add(parseExpression());
        }
        
        return expressions;
    }
    
    private Expression parseConstructorDeclaration()
    {
        String              visibility = assertGetThenNext(TokenType.CONSTRUCTOR);
        PrototypeExpression f          = parsePrototype(visibility, "constuctor");
        
        if (currentToken.getType() == TokenType.SEMICOLON)
        {
            assertGetThenNext(TokenType.SEMICOLON);
            List<Expression> list = new ArrayList<>();
            for (PrototypeParameter parameter : f.getParameters())
            {
                list.add(new VariableDefinitionExpression(parameter.getName(), parameter.getType()));
            }
            list.add(new ConstructorExpression(visibility, f, new BlockExpression(List.of(new NullExpression()))));
            return new MultiExpression(list);
        }
        
        BlockExpression b = parseBlockExpression();
        return new ConstructorExpression(visibility, f, b);
    }
    
    private Expression parseOperatorDeclaration()
    {
        String        visibility = assertGetThenNext(TokenType.OPERATOR);
        StringBuilder identifier = new StringBuilder();
        do
        {
            identifier.append(currentToken.getContent());
            nextToken();
        } while (currentToken.getType() != TokenType.WHITESPACE);
        nextTokenSkippingWhitespace();
        
        PrototypeExpression prototype = parseOperatorPrototype(visibility, identifier.toString());
        BlockExpression     body      = parseBlockExpression();
        return new OperatorExpression(visibility, prototype, body);
    }
    
    private Expression parseFunctionDeclaration()
    {
        String              visibility = assertGetThenNext(TokenType.FUNCTION);
        String              identifier = assertGetThenNext(TokenType.IDENTIFIER);
        PrototypeExpression prototype  = parsePrototype(visibility, identifier);
        BlockExpression     body       = parseBlockExpression();
        
        return new FunctionExpression(visibility, prototype, body);
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
                assertThenNext(TokenType.SEMICOLON);
            }
        }
        return b;
    }
    
    private Expression parseVariableDefinition()
    {
        String    visibility = currentToken.getContent();
        TokenType vType      = currentToken.getType();
        nextTokenSkippingWhitespace();
        
        if (vType != TokenType.VAR && vType != TokenType.CONST)
        {
            assertThenNext(TokenType.COLON);
        }
        
        String identifier = assertGetThenNext(TokenType.IDENTIFIER);
        
        return new VariableDefinitionExpression(identifier, visibility);
    }
    
    private Expression parsePrimary(boolean allowTernary, boolean allowVariableDeclaration)
    {
        switch (currentToken.getType())
        {
            case SQUIGLY:
                return parseIntrinsic();
            case NOT:
                return parseNotExpression();
            case LSQUIGLY:
                return parseBlockExpression();
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
            case BREAK:
                return parseBreak();
            case CONTINUE:
                return parseContinue();
            case CREATE:
            {
                return parseCreate();
            }
            case IDENTIFIER:
            {
                return parseIdentifierBlock(allowTernary, allowVariableDeclaration);
            }
            case PLUSPLUS:
            case MINUSMINUS:
                return parsePrePostOP();
            
            case DOTDOT:
                return parseDotdot();
            case FLOAT:
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
                return parseReturn();
            }
            case CONST:
            case VAR:
            {
                return parseVariableDefinition();
            }
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
            case CAST:
            {
                return parseCastDeclaration();
            }
            default:
                return null;
        }
    }
    
    private Expression parseIntrinsic()
    {
        assertThenNext(TokenType.SQUIGLY);
        String function = assertGetThenNext(TokenType.IDENTIFIER);
        assertType(TokenType.LPAREN);
        
        List<String> params = new ArrayList<>();
        do
        {
            nextTokenSkippingWhitespace();
            if (currentToken.getType() != TokenType.RPAREN)
            {
                String name = assertGetThenNext(TokenType.IDENTIFIER);
                params.add(name);
            }
        } while (currentToken.getType() == TokenType.COMMA);
        nextTokenSkippingWhitespace();
        
        return new IntrinsicExpression(function, params);
    }
    
    private Expression parseIdentifierBlock(boolean allowTernary, boolean allowVariableDeclaration)
    {
        Token next = peekToken(1);
        if (allowTernary)
        {
            if ((next.getType() == TokenType.QUESTIONMARK) || (next.getType() == TokenType.QUESTIONMARKCOLON))
            {
                return parseTernary();
            }
        }
        
        if (allowVariableDeclaration)
        {
            if (next.getType() == TokenType.COLON)
            {
                return parseVariableDefinition();
            }
        }
        
        
        if (next.getType() == TokenType.PLUSPLUS || next.getType() == TokenType.MINUSMINUS)
        {
            return parsePrePostOP();
        }
        
        if (next.getType() == TokenType.LBRACKET)
        {
            if (peekToken(2).getType() != TokenType.RBRACKET)
            {
                return parseArrayAccess();
            }
            return parseArrayVariableDefinition();
        }
        
        return parseIdentifier();
    }
    
    private Expression parseArrayVariableDefinition()
    {
        String    visibility = currentToken.getContent();
        TokenType vType      = currentToken.getType();
        nextTokenSkippingWhitespace();
        
        if (vType != TokenType.VAR && vType != TokenType.CONST)
        {
            assertThenNext(TokenType.LBRACKET);
            assertThenNext(TokenType.RBRACKET);
            assertThenNext(TokenType.COLON);
        }
        
        String identifier = assertGetThenNext(TokenType.IDENTIFIER);
        
        return new ArrayVariableDefinitionExpression(identifier, visibility);
    }
    
    private Expression parseCreate()
    {
        assertThenNext(TokenType.CREATE);
        Expression parsed = parseExpression();
        return new CreateExpression(parsed);
    }
    
    private Expression parseArrayAccess()
    {
        Expression variable = parseIdentifier();
        assertThenNext(TokenType.LBRACKET);
        Expression index = parseExpression();
        assertThenNext(TokenType.RBRACKET);
        
        return new ArrayAccessExpression(variable, index);
    }
    
    private Expression parseNotExpression()
    {
        assertThenNext(TokenType.NOT);
        IfContitionExpression negateMe = parseIfConditionExpression();
        return new NotExpression(negateMe);
    }
    
    private Expression parseBreak()
    {
        assertThenNext(TokenType.BREAK);
        return new BreakStatement();
    }
    
    private Expression parseContinue()
    {
        assertThenNext(TokenType.CONTINUE);
        return new ContinueStatement();
    }
    
    private Expression parseCastDeclaration()
    {
        assertThenNext(TokenType.CAST);
        List<Expression> casts = parseCastPrototype();
        BlockExpression  block = parseBlockExpression();
        return new CastExpression(casts, block);
    }
    
    private List<Expression> parseCastPrototype()
    {
        List<Expression> casts = new ArrayList<>();
        assertThenNext(TokenType.LPAREN);
        while (currentToken.getType() != TokenType.RPAREN)
        {
            Expression parsed = parseExpression();
            casts.add(parsed);
            if (currentToken.getType() != TokenType.RPAREN)
            {
                assertThenNext(TokenType.SEMICOLON);
            }
        }
        assertThenNext(TokenType.RPAREN);
        return casts;
    }
    
    private Expression parsePrePostOP()
    {
        if (currentToken.getType() == TokenType.PLUSPLUS || currentToken.getType() == TokenType.MINUSMINUS)
        {
            TokenType  type     = assertThenNext(TokenType.PLUSPLUS, TokenType.MINUSMINUS);
            Expression variable = parseIdentifier();
            return new PostOpExpression(variable, type);
        } else
        {
            Expression variable = parseIdentifier();
            TokenType  type     = assertThenNext(TokenType.PLUSPLUS, TokenType.MINUSMINUS);
            return new PreOpExpression(variable, type);
        }
    }
    
    private Expression parseReturn()
    {
        assertThenNext(TokenType.RETURN);
        Expression parsed = parseExpression();
        return new ReturnExpression(parsed);
    }
    
    private Expression parseText()
    {
        String value = assertGetThenNext(TokenType.TEXT);
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
            nextTokenSkippingWhitespace();
        }
        nextToken();
        nextToken();
        nextToken();
        return new TextExpression(sb.toString());
    }
    
    private BlockExpression parseBlockExpression()
    {
        assertThenNext(TokenType.LSQUIGLY);
        List<Expression> body = parseExpressionList();
        assertThenNext(TokenType.RSQUIGLY);
        return new BlockExpression(body);
    }
    
    private Expression parseDotdot()
    {
        String[] c = assertGetThenNext(TokenType.DOTDOT).split("\\.\\.");
        long     f = Long.parseUnsignedLong(c[0]);
        long     s = Long.parseUnsignedLong(c[1]);
        
        List<Object> params = new ArrayList<>();
        for (; f <= s; f++)
        {
            params.add(f);
        }
        
        return new ArrayExpression(params);
    }
    
    private Expression parseNumber()
    {
        if (currentToken.getType() == TokenType.NUMBER)
        {
            String content = assertGetThenNext(TokenType.NUMBER);
            long   num     = Long.parseUnsignedLong(content, 10);
            return new NumberExpression(BigInteger.valueOf(num), 64);
        }
        
        String content = assertGetThenNext(TokenType.FLOAT);
        double num     = Double.parseDouble(content);
        return new FloatExpression(num);
    }
    
    private Expression parseTernary()
    {
        Expression condition = parseExpression(false, false);
        
        if (currentToken.getType() == TokenType.QUESTIONMARK)
        {
            nextTokenSkippingWhitespace();
            Expression trueCond = parseExpression(true, false);
            assertThenNext(TokenType.COLON);
            Expression falseCond = parseExpression(true, false);
            
            return new TernaryExpression(condition, trueCond, falseCond);
        }
        
        if (currentToken.getType() == TokenType.QUESTIONMARKCOLON)
        {
            nextTokenSkippingWhitespace();
            Expression falseCond = parseExpression();
            return new TernaryExpression(condition, condition, falseCond);
        }
        
        logParseError("Invalid ternary syntax, expected ? or ?:");
        return null;
    }
    
    private Expression parseIf()
    {
        assertThenNext(TokenType.IF);
        assertThenNext(TokenType.LPAREN);
        
        IfContitionExpression condition = parseIfConditionExpression();
        
        assertThenNext(TokenType.RPAREN);
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression trueStatement = parseExpression();
            
            assertThenNext(TokenType.SEMICOLON);
            
            return parseElse(condition, List.of(trueStatement));
            
        } else
        {
            nextTokenSkippingWhitespace();
            List<Expression> trueStatements = new ArrayList<>();
            while (currentToken.getType() != TokenType.RSQUIGLY)
            {
                Expression parsed = parseExpression();
                trueStatements.add(parsed);
                
                assertThenNext(TokenType.SEMICOLON);
            }
            nextTokenSkippingWhitespace();
            
            return parseElse(condition, trueStatements);
        }
    }
    
    private IfContitionExpression parseIfConditionExpression()
    {
        return new IfContitionExpression(parseExpression());
    }
    
    private Expression parseElse(IfContitionExpression condition, List<Expression> trueStatements)
    {
        if (currentToken.getType() != TokenType.ELSE)
        {
            return new IfExpression(condition, trueStatements, List.of());
        }
        nextTokenSkippingWhitespace();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression falseStatement = parseExpression();
            
            assertThenNext(TokenType.SEMICOLON);
            
            return new IfExpression(condition, trueStatements, List.of(falseStatement));
        } else
        {
            nextTokenSkippingWhitespace();
            List<Expression> falseStatements = new ArrayList<>();
            while (currentToken.getType() != TokenType.RSQUIGLY)
            {
                Expression parsed = parseExpression();
                falseStatements.add(parsed);
                
                assertThenNext(TokenType.SEMICOLON);
            }
            nextTokenSkippingWhitespace();
            
            return new IfExpression(condition, trueStatements, falseStatements);
        }
    }
    
    private Expression parseDo()
    {
        assertThenNext(TokenType.DO);
        List<Expression> body = new ArrayList<>();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression doStatement = parseExpression();
            
            assertThenNext(TokenType.SEMICOLON);
            
            body.add(doStatement);
        } else
        {
            nextTokenSkippingWhitespace();
            while (currentToken.getType() != TokenType.RSQUIGLY)
            {
                Expression parsed = parseExpression();
                body.add(parsed);
                
                assertThenNext(TokenType.SEMICOLON);
            }
            nextTokenSkippingWhitespace();
        }
        
        assertThenNext(TokenType.WHILE);
        assertThenNext(TokenType.LPAREN);
        
        Expression condition = parseExpression();
        
        assertThenNext(TokenType.RPAREN);
        
        return parseThenDo(condition, body);
    }
    
    private Expression parseThenDo(Expression condition, List<Expression> doStatements)
    {
        if (currentToken.getType() != TokenType.THEN)
        {
            if (currentToken.getType() == TokenType.SEMICOLON)
            {
                nextTokenSkippingWhitespace();
            }
            
            return new DoWhileExpression(condition, doStatements);
        }
        nextTokenSkippingWhitespace();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression thenStatements = parseExpression();
            
            assertThenNext(TokenType.SEMICOLON);
            return new DoWhileThenExpression(condition, doStatements, List.of(thenStatements));
        } else
        {
            nextTokenSkippingWhitespace();
            List<Expression> falseStatements = new ArrayList<>();
            while (currentToken.getType() != TokenType.RSQUIGLY)
            {
                Expression parsed = parseExpression();
                falseStatements.add(parsed);
                
                assertThenNext(TokenType.SEMICOLON);
            }
            nextTokenSkippingWhitespace();
            
            return new DoWhileThenExpression(condition, doStatements, falseStatements);
        }
    }
    
    private Expression parseWhile()
    {
        assertThenNext(TokenType.WHILE);
        assertThenNext(TokenType.LPAREN);
        Expression condition = parseExpression();
        assertThenNext(TokenType.RPAREN);
        
        
        if (currentToken.getType() == TokenType.SEMICOLON)
        {
            nextTokenSkippingWhitespace();
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
                assertThenNext(TokenType.SEMICOLON);
            }
            
            return parseThenWhile(condition, List.of(doStatement));
        } else
        {
            nextTokenSkippingWhitespace();
            List<Expression> doStatements = new ArrayList<>();
            while (currentToken.getType() != TokenType.RSQUIGLY)
            {
                Expression parsed = parseExpression();
                doStatements.add(parsed);
                
                assertThenNext(TokenType.SEMICOLON);
            }
            nextTokenSkippingWhitespace();
            
            return parseThenWhile(condition, doStatements);
        }
    }
    
    private Expression parseThenWhile(Expression condition, List<Expression> doStatements)
    {
        if (currentToken.getType() != TokenType.THEN)
        {
            return new WhileExpression(condition, doStatements);
        }
        nextTokenSkippingWhitespace();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression thenStatements = parseExpression();
            
            assertThenNext(TokenType.SEMICOLON);
            
            return new WhileThenExpression(condition, doStatements, List.of(thenStatements));
        } else
        {
            nextTokenSkippingWhitespace();
            List<Expression> falseStatements = new ArrayList<>();
            while (currentToken.getType() != TokenType.RSQUIGLY)
            {
                Expression parsed = parseExpression();
                falseStatements.add(parsed);
                
                assertThenNext(TokenType.SEMICOLON);
            }
            nextTokenSkippingWhitespace();
            
            return new WhileThenExpression(condition, doStatements, falseStatements);
        }
    }
    
    
    private Expression parseFor()
    {
        assertThenNext(TokenType.FOR);
        assertThenNext(TokenType.LPAREN);
        
        List<Expression> init = parseCommaSeparatedExpressions(TokenType.SEMICOLON, TokenType.COLON);
        
        if (peekPrevToken().getType() == TokenType.COLON)
        {
            return parseForEach(init);
        }
        
        List<Expression> condition = parseCommaSeparatedExpressions(TokenType.SEMICOLON);
        List<Expression> increment = parseCommaSeparatedExpressions(TokenType.RPAREN);
        
        if (currentToken.getType() == TokenType.SEMICOLON)
        {
            nextTokenSkippingWhitespace();
            return parseThenFor(init, condition, increment, new NullExpression());
        }
        
        if (currentToken.getType() == TokenType.THEN)
        {
            return parseThenFor(init, condition, increment, new NullExpression());
        }
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression doStatement = parseExpression();
            
            if (!(doStatement instanceof ControlExpression))
            {
                assertThenNext(TokenType.SEMICOLON);
            }
            
            return parseThenFor(init, condition, increment, doStatement);
        } else
        {
            Expression block = parseBlockExpression();
            return parseThenFor(init, condition, increment, block);
        }
    }
    
    private Expression parseForEach(List<Expression> init)
    {
        Expression collection = parseExpression();
        assertThenNext(TokenType.RPAREN);
        
        if (currentToken.getType() == TokenType.SEMICOLON)
        {
            nextTokenSkippingWhitespace();
            return parseThenForEach(init, collection, new NullExpression());
        }
        
        if (currentToken.getType() == TokenType.THEN)
        {
            return parseThenForEach(init, collection, new NullExpression());
        }
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression doStatement = parseExpression();
            
            if (!(doStatement instanceof ControlExpression))
            {
                assertThenNext(TokenType.SEMICOLON);
            }
            
            return parseThenForEach(init, collection, doStatement);
        } else
        {
            Expression block = parseBlockExpression();
            return parseThenForEach(init, collection, block);
        }
    }
    
    private Expression parseThenForEach(List<Expression> init, Expression collection, Expression doStatements)
    {
        if (currentToken.getType() != TokenType.THEN)
        {
            return new ForEachExpression(init, collection, doStatements);
        }
        nextTokenSkippingWhitespace();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression thenStatements = parseExpression();
            
            assertThenNext(TokenType.SEMICOLON);
            
            return new ForEachThenExpression(init, collection, doStatements, thenStatements);
        } else
        {
            Expression block = parseBlockExpression();
            return new ForEachThenExpression(init, collection, doStatements, block);
        }
    }
    
    private List<Expression> parseCommaSeparatedExpressions(TokenType... endTokens)
    {
        List<TokenType>  ending = List.of(endTokens);
        List<Expression> data   = new ArrayList<>();
        
        while (!ending.contains(currentToken.getType()))
        {
            Expression parsed = parseExpression();
            data.add(parsed);
            
            if (ending.contains(currentToken.getType()))
            {
                nextTokenSkippingWhitespace();
                return data;
            }
            
            if (currentToken.getType() != TokenType.COMMA)
            {
                logParseError("Expected , in argument list");
            } else
            {
                nextTokenSkippingWhitespace();
            }
        }
        nextTokenSkippingWhitespace();
        return data;
    }
    
    private Expression parseThenFor(List<Expression> init, List<Expression> condition, List<Expression> increment, Expression doStatement)
    {
        if (currentToken.getType() != TokenType.THEN)
        {
            return new ForExpression(init, condition, increment, doStatement);
        }
        nextTokenSkippingWhitespace();
        
        if (currentToken.getType() != TokenType.LSQUIGLY)
        {
            Expression thenStatements = parseExpression();
            assertThenNext(TokenType.SEMICOLON);
            
            return new ForThenExpression(init, condition, increment, doStatement, thenStatements);
        } else
        {
            Expression block = parseBlockExpression();
            return new ForThenExpression(init, condition, increment, doStatement, block);
        }
    }
    
    
    private Expression parseEnum()
    {
        assertThenNext(TokenType.ENUM);
        String identifier = assertGetThenNext(TokenType.IDENTIFIER);
        String superClass = "object";
        
        if (currentToken.getType() == TokenType.EXTENDS)
        {
            assertThenNext(TokenType.EXTENDS);
            superClass = assertGetThenNext(TokenType.IDENTIFIER);
        }
        
        List<Expression> members = parseEnumMembers();
        List<Expression> methods = parseExpressionList();
        
        assertThenNext(TokenType.RSQUIGLY);
        return new EnumExpression(identifier, superClass, members, new ClassBlockExpression(methods));
    }
    
    private List<Expression> parseEnumMembers()
    {
        assertThenNext(TokenType.LSQUIGLY);
        List<Expression> members = new ArrayList<>();
        while (currentToken.getType() != TokenType.SEMICOLON)
        {
            String name = assertGetThenNext(TokenType.IDENTIFIER);
            
            if (currentToken.getType() == TokenType.LPAREN)
            {
                nextTokenSkippingWhitespace();
                List<Expression> params = new ArrayList<>();
                while (currentToken.getType() != TokenType.RPAREN)
                {
                    Expression parsed = parseExpression();
                    params.add(parsed);
                    
                    if (currentToken.getType() == TokenType.RPAREN)
                    {
                        break;
                    }
                    
                    assertThenNext(TokenType.COMMA);
                }
                nextTokenSkippingWhitespace();
                members.add(new EnumMemberExpression(name, params));
                if (currentToken.getType() == TokenType.COMMA)
                {
                    nextTokenSkippingWhitespace();
                    continue;
                }
            }
            
            if (currentToken.getType() == TokenType.COMMA)
            {
                members.add(new EnumMemberExpression(name, List.of()));
                nextTokenSkippingWhitespace();
            }
        }
        
        assertGetThenNext(TokenType.SEMICOLON);
        return members;
    }
    
    private Expression parseAssert()
    {
        nextTokenSkippingWhitespace();
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
            
            // parse custom operators
            List<TokenType> stopTypes = Arrays.asList(TokenType.LPAREN, TokenType.IDENTIFIER, TokenType.NUMBER, TokenType.TEXT, TokenType.FLOAT, TokenType.SWITCH, TokenType.FOR, TokenType.WHILE);
            StringBuilder   opb       = new StringBuilder();
            do
            {
                if (opb.length() != 0 && opb.length() + currentToken.getContent().length() > 2)
                {
                    break;
                }
                
                opb.append(currentToken.getContent());
                nextTokenSkippingWhitespace();
            } while (!stopTypes.contains(currentToken.getType()) && opb.length() != 2);
            
            TokenType  useType = TokenType.from(opb.toString());
            Token      op      = new Token(opb.toString(), useType, currentToken.getTokenLocation());
            Expression right   = parseExpression();
            
            int next = getPrecedence();
            if (precedence < next)
            {
                right = parseBinaryOps(precedence + 1, right);
            }
            
            left = new BinaryExpression(op, left, right);
        }
    }
    
    private Expression parseChainOps(Expression left, Token op)
    {
        List<Expression> exps = new ArrayList<>();
        exps.add(left);
        nextTokenSkippingWhitespace();
        
        while (true)
        {
            Expression e = parseExpression();
            if (e instanceof ChainCompareExpression)
            {
                ChainCompareExpression cc = (ChainCompareExpression) e;
                if (cc.getOperator().getType() == op.getType())
                {
                    exps.addAll(cc.getExpressions());
                } else
                {
                    exps.add(e);
                }
            } else
            {
                exps.add(e);
            }
            
            if (currentToken.getType() != op.getType())
            {
                break;
            }
            assertThenNext(op.getType());
        }
        
        return new ChainCompareExpression(exps, op);
    }
    
    private Expression parseParenthesis()
    {
        assertThenNext(TokenType.LPAREN);
        Expression v = parseExpression();
        assertThenNext(TokenType.RPAREN);
        return v;
    }
    
    private Expression parseIdentifier()
    {
        String id = assertGetThenNext(TokenType.IDENTIFIER);
        if (currentToken.getType() != TokenType.LPAREN)
        {
            return new VariableExpression(id);
        }
        
        nextTokenSkippingWhitespace();
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
                    logParseError("Expected , or ) in argument list");
                }
                
                nextTokenSkippingWhitespace();
            }
        }
        
        nextTokenSkippingWhitespace();
        
        return new CallExpression(id, parameters);
    }
    
    private Expression parseSwitch()
    {
        assertThenNext(TokenType.SWITCH);
        assertThenNext(TokenType.LPAREN);
        
        Expression e = parseExpression();
        
        assertThenNext(TokenType.RPAREN);
        assertThenNext(TokenType.LSQUIGLY);
        
        
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
                
                nextTokenSkippingWhitespace();
                Expression exp;
                
                if (currentToken.getType() == TokenType.LSQUIGLY)
                {
                    nextTokenSkippingWhitespace();
                    List<Object> exps = new ArrayList<>();
                    while (currentToken.getType() != TokenType.RSQUIGLY)
                    {
                        Expression parsed = parseExpression();
                        if (currentToken.getType() == TokenType.RSQUIGLY)
                        {
                            break;
                        }
                        
                        assertThenNext(TokenType.COMMA);
                    }
                    
                    nextTokenSkippingWhitespace();
                    exp = new ArrayExpression(exps);
                } else
                {
                    exp = parseExpression();
                }
                
                assertThenNext(TokenType.COLON);
                
                if (currentToken.getType() != TokenType.LSQUIGLY)
                {
                    assertType(TokenType.RETURN);
                    Expression body = parseExpression();
                    cases.add(new SwitchParameter(caseIndex, exp, body));
                    
                    assertThenNext(TokenType.SEMICOLON);
                    
                    continue;
                }
                
                Expression body = parseBlockExpression();
                cases.add(new SwitchParameter(caseIndex, exp, body));
            }
            
            if (currentToken.getType() == TokenType.DEFAULT)
            {
                nextTokenSkippingWhitespace();
                
                assertThenNext(TokenType.COLON);
                assertThenNext(TokenType.LSQUIGLY);
                
                Expression body = parseExpression();
                
                assertThenNext(TokenType.RSQUIGLY);
                assertThenNext(TokenType.SEMICOLON);
                
                defaultParam = new SwitchParameter(-1, condition, body);
            }
        }
        
        nextTokenSkippingWhitespace();
        
        return new SwitchExpression(cases, defaultParam);
    }
    
    private Expression parseExpression(boolean ternary, boolean allowVarDec)
    {
        Expression left = parsePrimary(ternary, allowVarDec);
        
        if (TokenType.isChainable(currentToken))
        {
            return parseChainOps(left, currentToken);
        }
        
        return parseBinaryOps(0, left);
    }
    
    
    private Expression parseExpression()
    {
        return parseExpression(true, true);
    }
    
    private PrototypeExpression parseOperatorPrototype(String visibility, String identifier)
    {
        assertType(TokenType.LPAREN);
        
        List<PrototypeParameter> params = new ArrayList<>();
        do
        {
            nextTokenSkippingWhitespace();
            if (currentToken.getType() != TokenType.RPAREN)
            {
                String clazz = assertGetThenNext(TokenType.IDENTIFIER);
                assertThenNext(TokenType.COLON);
                String name = assertGetThenNext(TokenType.IDENTIFIER);
                params.add(new PrototypeParameter(clazz, name));
            }
        } while (currentToken.getType() == TokenType.COMMA);
        assertThenNext(TokenType.RPAREN);
        
        if (currentToken.getType() == TokenType.LSQUIGLY || currentToken.getType() == TokenType.RETURN)
        {
            return new PrototypeExpression(identifier, params, "");
        }
        
        assertThenNext(TokenType.COLON);
        String returnType = assertGetThenNext(TokenType.IDENTIFIER);
        return new PrototypeExpression(identifier, params, returnType);
    }
    
    private PrototypeExpression parsePrototype(String visibility, String identifier)
    {
        assertType(TokenType.LPAREN);
        
        List<PrototypeParameter> params = new ArrayList<>();
        do
        {
            nextTokenSkippingWhitespace();
            if (currentToken.getType() != TokenType.RPAREN)
            {
                String clazz = assertGetThenNext(TokenType.IDENTIFIER);
                assertThenNext(TokenType.COLON);
                String name = assertGetThenNext(TokenType.IDENTIFIER);
                params.add(new PrototypeParameter(clazz, name));
            }
        } while (currentToken.getType() == TokenType.COMMA);
        nextTokenSkippingWhitespace();
        
        if (visibility.equals("constructor"))
        {
            if (currentToken.getType() == TokenType.SEMICOLON)
            {
                return new PrototypeExpression(identifier, params, identifier);
            }
        }
        
        if (currentToken.getType() != TokenType.COLON)
        {
            return new PrototypeExpression(identifier, params, "void");
        }
        
        assertThenNext(TokenType.COLON);
        String returnType = assertGetThenNext(TokenType.IDENTIFIER);
        return new PrototypeExpression(identifier, params, returnType);
    }
    
    private Expression parseImport()
    {
        nextTokenSkippingWhitespace();
        
        String classname = assertGetThenNext(TokenType.IDENTIFIER);
        assertThenNext(TokenType.FROM);
        String filename = assertGetThenNext(TokenType.TEXT);
        assertThenNext(TokenType.SEMICOLON);
        
        return new ImportExpression(classname, filename);
    }
}
