package lexer;

import java.util.*;
import java.util.regex.*;

public class Lexer
{
    
    FileLocation line;
    
    public List<Token> parse(String filename, String data)
    {
        this.line = new FileLocation(filename, 1, 1);
        TextIterator it     = new TextIterator(data);
        List<Token>  tokens = new ArrayList<>();
        
        while (it.hasNext())
        {
            tokens.add(getNextToken(it));
        }
        
        return tokens;
    }
    
    private Token getNextToken(TextIterator it)
    {
        // skip whitespace
        if (isWhitespace(it.current()))
        {
            while (isWhitespace(it.current()))
            {
                if (it.current().equals("\n"))
                {
                    line.nextLine();
                }
                
                line.moveIndex(it.current().length());
                it.next();
            }
            return new Token("", TokenType.WHITESPACE, line.copy());
        }
        
        // read identifiers
        if (isIdentifier(it.current()))
        {
            line.moveIndex(it.current().length());
            StringBuilder id = new StringBuilder(it.current());
            while (isIdentifier(id.toString()))
            {
                if (!canAppendToIdentifier(it.next()))
                {
                    break;
                }
                
                id.append(it.current());
                line.moveIndex(it.current().length());
            }
            
            Token keyword = getKeyword(id.toString());
            return keyword != null ? keyword : new Token(id.toString(), TokenType.IDENTIFIER, line.newWithOffset(-id.toString().length()));
        }
        
        // read digits
        if (isDigit(it.current()))
        {
            line.moveIndex(it.current().length());
            StringBuilder num = new StringBuilder(it.current());
            
            String type = it.next();
            line.moveIndex(it.current().length());
            if (type.equals(".") && it.peek().equals("."))
            {
                it.next();
                it.next();
                line.moveIndex(2);
                String startIndex = num.toString();
                String endIndex   = getNextToken(it).getContent();
                return new Token(startIndex + ".." + endIndex, TokenType.DOTDOT, line.newWithOffset(startIndex.length() + 2 + endIndex.length()));
            }
            
            // Allow _ in numbers
            if (isDigit(type) || type.equals("_") || type.equals("."))
            {
                num.append(type);
                while (isDigit(it.next()) || it.current().equals("_") || it.current().equals("."))
                {
                    line.moveIndex(it.current().length());
                    num.append(it.current());
                }
                
                String numb = num.toString().replace("_", "");
                if (numb.contains("."))
                {
                    while (numb.replace(".", "").length() + 1 != numb.length())
                    {
                        numb = numb.replaceFirst("\\.", "");
                    }
                    
                    return new Token(numb, TokenType.FLOAT, line.newWithOffset(-num.toString().length()));
                }
                
                return new Token(numb, TokenType.NUMBER, line.newWithOffset(-num.toString().length()));
            }
            
            // allow binary digits
            if (num.toString().equals("0") && type.equals("b"))
            {
                while (isBinary(it.next()))
                {
                    line.moveIndex(it.current().length());
                    num.append(it.current());
                }
                
                return new Token(String.valueOf(Long.parseUnsignedLong(num.toString(), 2)), TokenType.NUMBER, line.newWithOffset(-num.toString().length()));
            }
            
            // allow hex digits
            if (num.toString().equals("0") && type.equals("x"))
            {
                while (isHex(it.next()))
                {
                    line.moveIndex(it.current().length());
                    num.append(it.current());
                }
                
                return new Token(String.valueOf(Long.parseUnsignedLong(num.toString(), 16)), TokenType.NUMBER, line.newWithOffset(-num.toString().length()));
            }
            
            // allow octal digits
            if (num.toString().equals("0") && type.equals("o"))
            {
                while (isOctal(it.next()))
                {
                    line.moveIndex(it.current().length());
                    num.append(it.current());
                }
                
                return new Token(String.valueOf(Long.parseUnsignedLong(num.toString(), 8)), TokenType.NUMBER, line.newWithOffset(-num.toString().length()));
            }
            
            return new Token(num.toString(), TokenType.NUMBER, line.newWithOffset(-num.toString().length()));
        }
        
        if (isBlockCommentStart(it.current() + it.peek()))
        {
            StringBuilder comment = new StringBuilder(it.current());
            it.next();
            while (!isBlockCommentEnd(it.current() + it.peek()))
            {
                comment.append(it.current());
                line.moveIndex(it.current().length());
                it.next();
            }
            it.next();
            it.next();
            
            return new Token(comment.toString(), TokenType.COMMENT, line.newWithOffset(-comment.toString().length()));
        }
        
        if (isSingleLineComment(it.current()) && isSingleLineComment(it.peek()))
        {
            StringBuilder comment = new StringBuilder(it.current());
            it.next();
            while (!it.current().equals("\n"))
            {
                comment.append(it.current());
                line.moveIndex(it.current().length());
                it.next();
            }
            
            return new Token(comment.toString(), TokenType.COMMENT, line.newWithOffset(-comment.toString().length()));
        }
        
        // parse text
        if (isDoubleQuote(it.current()))
        {
            it.next();
            if (isDoubleQuote(it.current()) && !isDoubleQuote(it.peek()))
            {
                it.next();
                line.moveIndex(2);
                return new Token("", TokenType.TEXT, line.copy());
            } else if (!isDoubleQuote(it.current()))
            {
                StringBuilder sb = new StringBuilder();
                while (!isDoubleQuote(it.current()))
                {
                    sb.append(it.current());
                    line.moveIndex(it.current().length());
                    if (it.current().equals("\n"))
                    {
                        line.nextLine();
                    }
                    
                    it.next();
                }
                it.next();
                
                line.moveIndex(2);
                return new Token(sb.toString(), TokenType.TEXT, line.copy());
            } else if (isDoubleQuote(it.current()) && isDoubleQuote(it.peek()))
            {
                StringBuilder sb = new StringBuilder();
                it.next();
                it.next();
                line.moveIndex(2);
                
                boolean condition = !(isDoubleQuote(it.current()) &&
                                      isDoubleQuote(it.peek()) &&
                                      isDoubleQuote(it.peekTwo()));
                
                while (condition)
                {
                    sb.append(it.current());
                    
                    line.moveIndex(it.current().length());
                    if (it.current().equals("\n"))
                    {
                        line.nextLine();
                    }
                    
                    it.next();
                    
                    condition = !(isDoubleQuote(it.current()) &&
                                  isDoubleQuote(it.peek()) &&
                                  isDoubleQuote(it.peekTwo()));
                }
                
                it.next();
                it.next();
                it.next();
                line.moveIndex(3);
                return new Token(sb.toString(), TokenType.TEXT, line.copy());
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
        line.moveIndex(it.current().length());
        return new Token(val.toString(), TokenType.from(val.toString()), line.newWithOffset(-val.toString().length()));
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
    
    private boolean isSingleLineComment(String str)
    {
        Pattern pattern = Pattern.compile("[/]");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    private boolean isBlockCommentStart(String str)
    {
        Pattern pattern = Pattern.compile("/\\*");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    private boolean isBlockCommentEnd(String str)
    {
        Pattern pattern = Pattern.compile("\\*/");
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
        
        return new Token(str, type, line.copy());
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
