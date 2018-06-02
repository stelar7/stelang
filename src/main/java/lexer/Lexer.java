package lexer;

import ast.SyntaxTree;

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
                
                "    int d = 5;" +
                "    val c = 5;" +
                "    const a = 5;" +
                "    const b = switch(a) {" +
                "       case 1: {return 4};" +
                "       case 2..4: {return 3};" +
                "       case 5: {return 2};" +
                "}" +
                
                "    function add(int:a, int:b):int {" +
                "        if (a) return a; else return b;" +
                "    }" +
                
                "    function add(int:a, int:b):int {" +
                "        return a ? a : b" +
                "    }" +
                
                "    function add(int:a, int:b):int {" +
                "        return a + b;" +
                "    }" +
                
                "    pure add(int:a, int:b):int {" +
                "        return a + b;" +
                "    }" +
                
                "    global add(int:a, int:b):int {" +
                "        return a + b;" +
                "    }" +
                
                "    operator¤(myclass:self,otherclass:other):myclass {" +
                "        return myclass();" +
                "    }" +
                
                "    operator<=>(myclass:self,otherclass:other):myclass {" +
                "        return myclass();" +
                "    }" +
                "}");
        
        tokens.forEach(System.out::println);
        SyntaxTree s = new SyntaxTree(tokens);
        s.isValid();
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
    
    int lineNumber = 0;
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
            while (isIdentifier(it.next()))
            {
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
            
            // Allow _ in numbers
            String type = it.next();
            if (isDigit(type) || type.equals("_") || type.equals("."))
            {
                lineIndex += it.current().length();
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
                    
                    return new Token(numb, TokenType.NUMBER, lineNumber, lineIndex - num.toString().length());
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
    
    private boolean isWhitespace(String str)
    {
        Pattern pattern = Pattern.compile("[\\s]+");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
