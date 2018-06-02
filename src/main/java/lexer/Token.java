package lexer;

public class Token
{
    private String content;
    
    private int line;
    private int index;
    
    private TokenType type;
    
    public Token(String content, TokenType type, int line, int lineIndex)
    {
        this.content = content;
        this.type = type;
        this.line = line;
        this.index = lineIndex;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public TokenType getType()
    {
        return type;
    }
    
    public int getLine()
    {
        return line;
    }
    
    public int getIndex()
    {
        return index;
    }
    
    @Override
    public String toString()
    {
        return "Token{" +
               "content='" + content + '\'' +
               ", line=" + line +
               ", index=" + index +
               ", type=" + type +
               '}';
    }
}
