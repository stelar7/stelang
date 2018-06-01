package lexer;

import java.util.*;
import java.util.regex.*;

public class Lexer
{
    public static void main(String[] args)
    {
        Lexer l = new Lexer();
        // List<Token> oktokens = l.parse("this is some text 0x00 0xf 0xffff 0 15 65535 0b0 0b1111 0b1111111111111111");
//        List<Token> tokens = l.parse("// nt\nmore content starts here");
        
        List<Token> tokens = l.parse(
                "" +
                "class myclass {" +
                "    function add(int:a, int:b):int {" +
                "        return a + b;" +
                "    }" +
                
                "    operator¤(myclass:self,otherclass:other):myclass {" +
                "        return myclass();" +
                "    }" +
                "}");
        
        tokens.forEach(System.out::println);
    }
    
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
    
    private Token getNextToken(TextIterator it)
    {
        // skip whitespace
        while (isWhitespace(it.current()))
        {
            it.next();
        }
        
        // read identifiers
        if (isIdentifier(it.current()))
        {
            StringBuilder id = new StringBuilder(it.current());
            while (isIdentifier(it.next()))
            {
                id.append(it.current());
            }
            
            String keyword = getKeyword(id.toString());
            return keyword != null ? new Token(keyword, TokenType.KEYWORD) : new Token(id.toString(), TokenType.IDENTIFIER);
        }
        
        // read digits
        if (isDigit(it.current()))
        {
            StringBuilder num = new StringBuilder(it.current());
            
            // Allow _ in numbers
            String type = it.next();
            if (isDigit(type) || type.equals("_") || type.equals("."))
            {
                num.append(type);
                while (isDigit(it.next()) || it.current().equals("_") || it.current().equals("."))
                {
                    num.append(it.current());
                }
                
                String numb = num.toString().replace("_", "");
                if (numb.contains("."))
                {
                    while (numb.replace(".", "").length() + 1 != numb.length())
                    {
                        numb = numb.replaceFirst(".", "");
                    }
                    
                    return new Token(numb, TokenType.FLOAT);
                }
                
                return new Token(numb, TokenType.INT);
            }
            
            // allow binary digits
            if (num.toString().equals("0") && type.equals("b"))
            {
                while (isBinary(it.next()))
                {
                    num.append(it.current());
                }
                
                return new Token(String.valueOf(Long.parseUnsignedLong(num.toString(), 2)), TokenType.INT);
            }
            
            // allow hex digits
            if (num.toString().equals("0") && type.equals("x"))
            {
                while (isHex(it.next()))
                {
                    num.append(it.current());
                }
                
                return new Token(String.valueOf(Long.parseUnsignedLong(num.toString(), 16)), TokenType.INT);
            }
            
            // allow octal digits
            if (num.toString().equals("0") && type.equals("o"))
            {
                while (isOctal(it.next()))
                {
                    num.append(it.current());
                }
                
                return new Token(String.valueOf(Long.parseUnsignedLong(num.toString(), 8)), TokenType.INT);
            }
            
            return new Token(num.toString(), TokenType.INT);
        }
        
        // parse comments
        if (isComment(it.current()) && isComment(it.peek()))
        {
            it.next();
            StringBuilder comment = new StringBuilder();
            while (!it.current().equals("\n"))
            {
                comment.append(it.current());
                it.next();
            }
            
            return new Token(comment.toString(), TokenType.COMMENT);
        }
        
        String val = it.current();
        it.next();
        return new Token(val, TokenType.from(val));
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
    
    private String getKeyword(String str)
    {
        List<String> keywords = Arrays.asList("class", "function", "enum", "operator", "global", "pure",
                                              "if", "for", "while", "then", "continue", "break",
                                              "switch", "case", "default", "const", "val", "return"
                                             );
        
        if (keywords.contains(str))
        {
            return str;
        }
        
        return null;
    }
    
    private boolean isIdentifier(String str)
    {
        Pattern pattern = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");
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
