package lexer;

public class FileLocation
{
    private int    line;
    private int    index;
    private String filename;
    
    public FileLocation(String filename, int line, int index)
    {
        this.filename = filename;
        this.line = line;
        this.index = index;
    }
    
    
    public FileLocation newWithOffset(int index)
    {
        return new FileLocation(filename, line, this.index + index);
    }
    
    public FileLocation copy()
    {
        return new FileLocation(filename, line, index);
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
        return String.format("%s (column %s; file: %s)", line, index, filename);
    }
    
}
