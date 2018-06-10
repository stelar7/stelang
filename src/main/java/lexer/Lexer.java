package lexer;

import java.util.*;
import java.util.regex.*;

public class Lexer
{
    
    public List<Token> parse(String text)
    {
        TextIterator it     = new TextIterator(text);
        List<Token>  tokens = new ArrayList<>();
        
        while (it.hasNext())
        {
            tokens.add(getNextToken(it));
        }
        
        return tokens;
    }
    
    int lineNumber = 1;
    int lineIndex  = 0;
    
    private Token getNextToken(TextIterator it)
    {
        // skip whitespace
        while (isWhitespace(it.current()))
        {
            if (it.current().equals("\n"))
            {
                lineNumber++;
                lineIndex = 0;
            }
            
            lineIndex += it.current().length();
            it.next();
        }
        
        // read identifiers
        if (isIdentifier(it.current()))
        {
            lineIndex += it.current().length();
            StringBuilder id = new StringBuilder(it.current());
            while (isIdentifier(id.toString()))
            {
                if (!canAppendToIdentifier(it.next()))
                {
                    break;
                }
                
                id.append(it.current());
                lineIndex += it.current().length();
            }
            
            Token keyword = getKeyword(id.toString());
            return keyword != null ? keyword : new Token(id.toString(), TokenType.IDENTIFIER, lineNumber, lineIndex - id.toString().length());
        }
        
        // read digits
        if (isDigit(it.current()))
        {
            lineIndex += it.current().length();
            StringBuilder num = new StringBuilder(it.current());
            
            String type = it.next();
            lineIndex += it.current().length();
            if (type.equals(".") && it.peek().equals("."))
            {
                it.next();
                it.next();
                lineIndex += 2;
                String startIndex = num.toString();
                String endIndex   = getNextToken(it).getContent();
                return new Token(startIndex + ".." + endIndex, TokenType.DOTDOT, lineNumber, lineIndex - (startIndex.length() + 2 + endIndex.length()));
            }
            
            // Allow _ in numbers
            if (isDigit(type) || type.equals("_") || type.equals("."))
            {
                num.append(type);
                while (isDigit(it.next()) || it.current().equals("_") || it.current().equals("."))
                {
                    lineIndex += it.current().length();
                    num.append(it.current());
                }
                
                String numb = num.toString().replace("_", "");
                if (numb.contains("."))
                {
                    while (numb.replace(".", "").length() + 1 != numb.length())
                    {
                        numb = numb.replaceFirst("\\.", "");
                    }
                    
                    return new Token(numb, TokenType.FLOAT, lineNumber, lineIndex - num.toString().length());
                }
                
                return new Token(numb, TokenType.NUMBER, lineNumber, lineIndex - num.toString().length());
            }
            
            // allow binary digits
            if (num.toString().equals("0") && type.equals("b"))
            {
                while (isBinary(it.next()))
                {
                    lineIndex += it.current().length();
                    num.append(it.current());
                }
                
                return new Token(String.valueOf(Long.parseUnsignedLong(num.toString(), 2)), TokenType.NUMBER, lineNumber, lineIndex - num.toString().length());
            }
            
            // allow hex digits
            if (num.toString().equals("0") && type.equals("x"))
            {
                while (isHex(it.next()))
                {
                    lineIndex += it.current().length();
                    num.append(it.current());
                }
                
                return new Token(String.valueOf(Long.parseUnsignedLong(num.toString(), 16)), TokenType.NUMBER, lineNumber, lineIndex - num.toString().length());
            }
            
            // allow octal digits
            if (num.toString().equals("0") && type.equals("o"))
            {
                while (isOctal(it.next()))
                {
                    lineIndex += it.current().length();
                    num.append(it.current());
                }
                
                return new Token(String.valueOf(Long.parseUnsignedLong(num.toString(), 8)), TokenType.NUMBER, lineNumber, lineIndex - num.toString().length());
            }
            
            return new Token(num.toString(), TokenType.NUMBER, lineNumber, lineIndex - num.toString().length());
        }
        
        // parse comments
        if (isComment(it.current()) && isComment(it.peek()))
        {
            it.next();
            StringBuilder comment = new StringBuilder();
            while (!it.current().equals("\n"))
            {
                comment.append(it.current());
                lineIndex += it.current().length();
                it.next();
            }
            
            return new Token(comment.toString(), TokenType.COMMENT, lineNumber, lineIndex - comment.toString().length());
        }
        
        // parse text
        if (isDoubleQuote(it.current()))
        {
            it.next();
            if (isDoubleQuote(it.current()) && !isDoubleQuote(it.peek()))
            {
                it.next();
                lineIndex += 2;
                return new Token("", TokenType.TEXT, lineNumber, lineIndex);
            } else if (!isDoubleQuote(it.current()))
            {
                StringBuilder sb = new StringBuilder();
                while (!isDoubleQuote(it.current()))
                {
                    sb.append(it.current());
                    lineIndex += it.current().length();
                    if (it.current().equals("\n"))
                    {
                        lineNumber++;
                        lineIndex = 0;
                    }
                    
                    it.next();
                }
                
                it.next();
                return new Token(sb.toString(), TokenType.TEXT, lineNumber, lineIndex);
            } else if (isDoubleQuote(it.current()) && isDoubleQuote(it.peek()))
            {
                StringBuilder sb = new StringBuilder();
                it.next();
                it.next();
                
                boolean condition = !(isDoubleQuote(it.current()) &&
                                      isDoubleQuote(it.peek()) &&
                                      isDoubleQuote(it.peekTwo()));
                
                while (condition)
                {
                    sb.append(it.current());
                    
                    lineIndex += it.current().length();
                    if (it.current().equals("\n"))
                    {
                        lineNumber++;
                        lineIndex = 0;
                    }
                    
                    it.next();
                    
                    condition = !(isDoubleQuote(it.current()) &&
                                  isDoubleQuote(it.peek()) &&
                                  isDoubleQuote(it.peekTwo()));
                }
                
                it.next();
                it.next();
                it.next();
                return new Token(sb.toString(), TokenType.TEXT, lineNumber, lineIndex);
            }
        }
        
        
        StringBuilder val = new StringBuilder(it.current());
        it.next();
        
        String nval = it.current();
        
        if (TokenType.canCompound(val.toString(), nval))
        {
            while (TokenType.canCompound(val.toString(), nval))
            {
                val.append(nval);
                it.next();
                nval = it.current();
            }
        }
        lineIndex += it.current().length();
        return new Token(val.toString(), TokenType.from(val.toString()), lineNumber, lineIndex - val.toString().length());
    }
    
    private boolean isDoubleQuote(String str)
    {
        Pattern pattern = Pattern.compile("\"");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    private boolean isTripleDoubleQuote(String str)
    {
        Pattern pattern = Pattern.compile("\"{3}");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    private boolean isOctal(String str)
    {
        Pattern pattern = Pattern.compile("[0-7]");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    private boolean isComment(String str)
    {
        Pattern pattern = Pattern.compile("[/]");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    private boolean isHex(String str)
    {
        Pattern pattern = Pattern.compile("[a-fA-F0-9]");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    private boolean isBinary(String str)
    {
        Pattern pattern = Pattern.compile("[01]");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    private boolean isDigit(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    private Token getKeyword(String str)
    {
        TokenType type = TokenType.from(str);
        if (type == TokenType.UNKNOWN)
        {
            return null;
        }
        
        return new Token(str, type, lineNumber, lineIndex);
    }
    
    private boolean isIdentifier(String str)
    {
        Pattern pattern = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    private boolean canAppendToIdentifier(String str)
    {
        Pattern pattern = Pattern.compile("[_a-zA-Z0-9]*");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    private boolean isWhitespace(String str)
    {
        Pattern pattern = Pattern.compile("[\\s]+");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
