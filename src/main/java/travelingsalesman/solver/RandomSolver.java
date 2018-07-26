package travelingsalesman.solver;

import org.joml.*;
import renderer.*;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class RandomSolver extends TravelingSalesmanSolver
{
    private List<Vector2f> bestCities = new ArrayList<>();
    private Random         random     = new Random();
    
    public RandomSolver(List<Vector2f> cities, int h, int w)
    {
        super(cities, w, h);
    }
    
    @Override
    public void nextStep()
    {
        if (isSolved())
        {
            return;
        }
        
        setTriedpermutations(triedpermutations + 1);
        final float percentDone = (float) getPermutationsDone() / factorial(getCities().size());
        setPercent(percentDone);
        // assume this is done when we have tried n! combinations
        setSolved(percentDone > 1);
        
        
        Collections.swap(getCities(), random.nextInt(getCities().size()), random.nextInt(getCities().size()));
        
        int dist = distance(getCities());
        if (dist < bestDistance)
        {
            bestCities = new ArrayList<>(getCities());
            bestDistance = dist;
        }
        
    }
    
    
    private int distance(List<Vector2f> cities)
    {
        int sum = 0;
        
        for (int i = 0; i < cities.size() - 1; i++)
        {
            sum += cities.get(i).distance(cities.get(i + 1));
        }
        return sum;
    }
    
    @Override
    public void render()
    {
        GL11.glViewport(0, HEIGHT / 2, WIDTH, HEIGHT / 2);
        GL11.glColor3f(1, 1, 1);
        GL11.glLineWidth(1);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (Vector2f city : getCities())
        {
            GL11.glVertex3f(city.x, city.y, 0);
        }
        GL11.glEnd();
        for (Vector2f city1 : getCities())
        {
            Shapes.drawFilledCircle(city1, 8);
        }
        
        GL11.glViewport(0, 0, WIDTH, HEIGHT / 2);
        GL11.glColor3f(0.7f, 1f, 0.5f);
        GL11.glLineWidth(5);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (Vector2f city : bestCities)
        {
            GL11.glVertex3f(city.x, city.y, 0);
        }
        GL11.glEnd();
        for (Vector2f city1 : getCities())
        {
            Shapes.drawFilledCircle(city1, 8);
        }
        
        
    }
}
    