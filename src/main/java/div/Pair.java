package div;

import java.util.Objects;

public class Pair<X, Y>
{
    X x;
    Y y;
    
    public Pair(X x, Y y)
    {
        this.x = x;
        this.y = y;
    }
    
    public X getX()
    {
        return x;
    }
    
    public void setX(X x)
    {
        this.x = x;
    }
    
    public Y getY()
    {
        return y;
    }
    
    public void setY(Y y)
    {
        this.y = y;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(x, pair.x) &&
               Objects.equals(y, pair.y);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString()
    {
        return "Pair{" +
               "x=" + x +
               ", y=" + y +
               '}';
    }
}
