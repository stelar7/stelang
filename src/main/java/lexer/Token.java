package lexer;

public class Token
{
    private int       line;
    private String    content;
    private TokenType type;
    
    public Token(String content, TokenType type, int line)
    {
        this.content = content;
        this.type = type;
        this.line = line;
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
    
    @Override
    public String toString()
    {
        return "Token{" +
               "line=" + line +
               ", content='" + content + '\'' +
               ", type=" + type +
               '}';
    }
}
