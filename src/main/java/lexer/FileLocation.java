package lexer;

public class FileLocation
{
    private int line;
    private int index;
    
    public FileLocation(int line, int index)
    {
        this.line = line;
        this.index = index;
    }
    
    
    public FileLocation newWithOffset(int index)
    {
        return new FileLocation(line, this.index + index);
    }
    
    public FileLocation copy()
    {
        return new FileLocation(line, index);
    }
    
    public void nextLine()
    {
        line++;
        index = 0;
    }
    
    public void moveIndex(int offset)
    {
        index += offset;
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
        return String.format("%s (%s)", line, index);
    }
    
}
