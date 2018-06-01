public class TextIterator
{
    private int    current;
    private int    pos;
    private String text;
    
    public TextIterator(String text)
    {
        this.pos = 0;
        this.text = text;
        current = charAt(pos);
    }
    
    public String next()
    {
        pos++;
        current = charAt(pos);
        return current();
    }
    
    public String peek()
    {
        int point = charAt(pos + 1);
        return codepointToString(point);
    }
    
    public String current()
    {
        return codepointToString(current);
    }
    
    private String codepointToString(int point)
    {
        if (point != -1)
        {
            return new StringBuffer().appendCodePoint(point).toString();
        }
        
        return Character.toString('\03');
    }
    
    private int charAt(int pos)
    {
        if (pos < text.length())
        {
            return text.codePointAt(pos);
        }
        
        return -1;
    }
    
    public boolean hasNext()
    {
        return current != -1;
    }
    
}
