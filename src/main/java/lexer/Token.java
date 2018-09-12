package lexer;

public class Token
{
    private String    content;
    private TokenType type;
    
    private FileLocation line;
    
    public Token(String content, TokenType type, FileLocation line)
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
    
    public FileLocation getTokenLocation()
    {
        return line;
    }
    
    @Override
    public String toString()
    {
        return "Token{" +
               "content='" + content + '\'' +
               ", type=\"" + type +
               "\", line=" + line +
               '}';
    }
}
