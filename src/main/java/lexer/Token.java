package lexer;

public class Token
{
    private String    content;
    private TokenType type;
    
    public Token(String content, TokenType type)
    {
        this.content = content;
        this.type = type;
    }
    
    @Override
    public String toString()
    {
        return "Token{" +
               "content='" + content + '\'' +
               ", type=" + type +
               '}';
    }
}
