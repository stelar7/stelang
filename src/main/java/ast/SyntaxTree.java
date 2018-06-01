package ast;

import ast.exprs.*;
import lexer.*;

import java.util.*;

public class SyntaxTree
{
    private List<Token> tokens;
    private int         index;
    
    private Token currentToken;
    
    private static Map<TokenType, Integer> binOps = new HashMap<>()
    {{
        put(TokenType.UNKNOWN, 1);
        
        put(TokenType.PLUS, 2);
        put(TokenType.MINUS, 2);
        
        put(TokenType.ASTERISK, 3);
        put(TokenType.SLASH, 3);
        
        put(TokenType.SPACESHIP, 4);
        
        put(TokenType.LESS, 5);
        put(TokenType.GREATER, 5);
        put(TokenType.LESSEQL, 5);
        put(TokenType.GREATEREQL, 5);
        
        put(TokenType.EQUAL, 6);
        put(TokenType.NOTEQL, 6);
        
        put(TokenType.AMPERSAND, 7);
        put(TokenType.CARET, 8);
        put(TokenType.BAR, 9);
        put(TokenType.AMPERSANDAMPERSAND, 10);
        put(TokenType.BARBAR, 11);
        
        put(TokenType.SET, 12);
    }};
    
    private void nextToken()
    {
        currentToken = tokens.get(index++);
    }
    
    private Expression parseSingle()
    {
        switch (currentToken.getType())
        {
            case INT:
            case FLOAT:
                return parseNumberExpression();
            
            case IDENTIFIER:
                return parseIdentifierExpression();
            
            case LPAREN:
                return parseParenthesisExpression();
            
            default:
                System.err.println("Unknown token when expecting an expression: " + currentToken);
        }
        return null;
    }
    
    private Expression parseNumberExpression()
    {
        Expression e = new NumberExpression(Double.parseDouble(currentToken.getContent()));
        nextToken();
        return e;
    }
    
    private Expression parseParenthesisExpression()
    {
        nextToken();
        
        Expression e = parseSingle();
        if (e == null)
        {
            return null;
        }
        
        if (currentToken.getType() != TokenType.RPAREN)
        {
            System.err.println("Expected ) at end of expression");
            return null;
        }
        
        nextToken();
        return e;
    }
    
    private Expression parseIdentifierExpression()
    {
        String name = currentToken.getContent();
        nextToken();
        
        if (currentToken.getType() != TokenType.LPAREN)
        {
            return new VariableExpression(name);
        }
        
        nextToken();
        List<Expression> arguments = new ArrayList<>();
        if (currentToken.getType() != TokenType.RPAREN)
        {
            while (true)
            {
                Expression arg = parseSingle();
                if (arg != null)
                {
                    arguments.add(arg);
                } else
                {
                    return null;
                }
                
                if (currentToken.getType() == TokenType.RPAREN)
                {
                    break;
                }
                
                if (currentToken.getType() != TokenType.COMMA)
                {
                    System.err.println("Expected , or ) in argument list");
                    return null;
                }
                
                nextToken();
            }
        }
        
        nextToken();
        return new CallExpression(name, arguments);
    }
    
    private Expression parseExpression()
    {
        Expression left = parseSingle();
        if (left == null)
        {
            return null;
        }
        
        return parseBinOpRight(0, left);
    }
    
    private Expression parseBinOpRight(int maxPrecedence, Expression left)
    {
        while (true)
        {
            int tokenPrecedence = getPrecedence();
            
            if (tokenPrecedence < maxPrecedence)
            {
                return left;
            }
            
            Token op = currentToken;
            nextToken();
            
            Expression right = parseSingle();
            if (right == null)
            {
                return null;
            }
            
            int nextPrecedence = getPrecedence();
            if (tokenPrecedence < nextPrecedence)
            {
                right = parseBinOpRight(tokenPrecedence + 1, right);
                if (right == null)
                {
                    return null;
                }
            }
            
            left = new BinaryExpression(op, left, right);
        }
    }
    
    private PrototypeSyntax parsePrototype()
    {
        if (currentToken.getType() != TokenType.IDENTIFIER)
        {
            System.err.println("Expected function name");
            return null;
        }
        
        String name = currentToken.getContent();
        nextToken();
        
        if (currentToken.getType() != TokenType.LPAREN)
        {
            System.err.println("Expected ( in function declaration");
            return null;
        }
        
        List<PrototypeParameter> arguments = new ArrayList<>();
        while (true)
        {
            nextToken();
            if (currentToken.getType() == TokenType.IDENTIFIER)
            {
                String type = currentToken.getContent();
                
                nextToken();
                if (currentToken.getType() != TokenType.COLON)
                {
                    System.err.println("Expected : after type declaration");
                    return null;
                }
                
                nextToken();
                if (currentToken.getType() != TokenType.IDENTIFIER)
                {
                    System.err.println("Expected parameter name after :");
                    return null;
                }
                
                String paramName = currentToken.getContent();
                arguments.add(new PrototypeParameter(type, paramName));
                
                nextToken();
                if (currentToken.getType() == TokenType.RPAREN)
                {
                    break;
                }
            } else
            {
                if (currentToken.getType() != TokenType.IDENTIFIER)
                {
                    System.err.println("Expected parameter type");
                    return null;
                }
            }
        }
        
        return new PrototypeSyntax(name, arguments);
    }
    
    private FunctionSyntax parseDefinition()
    {
        if (currentToken.getType() != TokenType.FUNCTION)
        {
            System.err.println("Function declarations should start with the \"function\" keyword");
            return null;
        }
        
        nextToken();
        PrototypeSyntax proto = parsePrototype();
        if (proto == null)
        {
            return null;
        }
        
        Expression e = parseExpression();
        if (e == null)
        {
            return null;
        }
        
        return new FunctionSyntax(proto, e);
    }
    
    private FunctionSyntax parseTopLevelExpression()
    {
        Expression e = parseExpression();
        if (e == null)
        {
            return null;
        }
        
        return new FunctionSyntax(new PrototypeSyntax("", new ArrayList<>()), e);
    }
    
    private int getPrecedence()
    {
        return binOps.getOrDefault(currentToken.getType(), -1);
    }
}
