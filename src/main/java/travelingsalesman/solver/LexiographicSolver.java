package travelingsalesman.solver;

import org.joml.*;
import renderer.*;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class LexiographicSolver extends TravelingSalesmanSolver
{
    
    private Integer[] order;
    private Integer[] bestOrder;
    
    public LexiographicSolver(final List<Vector2f> cities, final int w, final int h)
    {
        super(cities, w, h);
        
        order = new Integer[cities.size()];
        bestOrder = new Integer[cities.size()];
        
        for (int i = 0; i < cities.size(); i++)
        {
            order[i] = bestOrder[i] = i;
        }
        
    }
    
    
    @Override
    public void nextStep()
    {
        if (isSolved())
        {
            return;
        }
        
        
        triedpermutations++;
        final float percentDone = (float) getPermutationsDone() / factorial(getCities().size());
        setPercent(percentDone);
        setSolved(percentDone > 1);
        
        int i = order.length - 1;
        while (i > 0 && order[i - 1] >= order[i])
        {
            i--;
        }
        if (i <= 0)
        {
            return;
        }
        int j = order.length - 1;
        while (order[j] <= order[i - 1])
        {
            j--;
        }
        int temp = order[i - 1];
        order[i - 1] = order[j];
        order[j] = temp;
        j = order.length - 1;
        while (i < j)
        {
            temp = order[i];
            order[i] = order[j];
            order[j] = temp;
            i++;
            j--;
        }
        
        
        int dist = distance(getCities(), order);
        
        if (dist < bestDistance)
        {
            System.arraycopy(order, 0, bestOrder, 0, order.length);
            bestDistance = dist;
        }
    }
    
    private int distance(List<Vector2f> cities, Integer[] order)
    {
        int sum = 0;
        
        for (int i = 0; i < order.length - 1; i++)
        {
            Vector2f a = cities.get(order[i]);
            Vector2f b = cities.get(order[i + 1]);
            sum += a.distance(b);
        }
        
        return sum;
    }
    
    @Override
    public void render()
    {
        glViewport(0, HEIGHT / 2, WIDTH, HEIGHT / 2);
        glColor3f(1, 1, 1);
        glLineWidth(1);
        glBegin(GL_LINE_STRIP);
        for (final Integer o : order)
        {
            glVertex3f(getCities().get(o).x, getCities().get(o).y, 0);
        }
        glEnd();
        for (Vector2f city1 : getCities())
        {
            Shapes.drawFilledCircle(city1, 8);
        }
        
        glViewport(0, 0, WIDTH, HEIGHT / 2);
        glColor3f(0.7f, 1f, 0.5f);
        glLineWidth(5);
        glBegin(GL_LINE_STRIP);
        for (final Integer o : bestOrder)
        {
            glVertex3f(getCities().get(o).x, getCities().get(o).y, 0);
        }
        glEnd();
        
        for (Vector2f city1 : getCities())
        {
            Shapes.drawFilledCircle(city1, 10);
        }
        
    }
    
}
