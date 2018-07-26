package travelingsalesman;

import org.joml.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import renderer.*;
import travelingsalesman.solver.*;

import java.text.*;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class TravelingSalesman
{
    private final Object         lock   = new Object();
    private       List<Vector2f> cities = new ArrayList<>();
    private       Random         random = new Random();
    private TravelingSalesmanSolver solver;
    private long                    window;
    private int      WIDTH  = 800;
    private int      HEIGHT = 600;
    private Vector2f cursor = new Vector2f();
    
    private boolean shouldClose = false;
    
    public static void main(String[] args)
    {
        new TravelingSalesman().run();
    }
    
    public void run()
    {
        try
        {
            init();
            
            new Thread(this::loop).start();
            
            while (!shouldClose)
            {
                GLFW.glfwWaitEvents();
            }
            
            synchronized (lock)
            {
                Callbacks.glfwFreeCallbacks(window);
                GLFW.glfwDestroyWindow(window);
            }
        } finally
        {
            GLFW.glfwTerminate();
            GLFW.glfwSetErrorCallback(null).free();
        }
    }
    
    private void init()
    {
        GLFWErrorCallback.createPrint(System.err).set();
        
        if (!GLFW.glfwInit())
        {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        
        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL)
        {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        
        GLFW.glfwSetCursorPosCallback(window, (windowPtr, x, y) -> cursor.set((float) x, (float) y));
        GLFW.glfwSetMouseButtonCallback(window, (windowPtr, button, action, mods) ->
        {
            if (action == GLFW.GLFW_RELEASE && button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                if (cursor.y > HEIGHT / 2 || cursor.y < 0)
                {
                    return;
                }
                
                cities.add(new Vector2f(cursor).mul(1, 2f));
                solver = new LexiographicSolver(cities, WIDTH, HEIGHT);
            }
        });
        
        
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        
        GLFW.glfwSetWindowPos(window, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);
        
        GLFW.glfwShowWindow(window);
        
    }
    
    private void loop()
    {
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        initPostGL();
        
        int updatesPerSecond = 100000;
        int skipInterval     = 1000 / updatesPerSecond;
        int maxFramesSkipped = 1000 * 2000;
        
        long timer = System.currentTimeMillis();
        int  loops;
        
        long fpstimer = System.currentTimeMillis();
        int  ups      = 0;
        int  fps      = 0;
        
        DecimalFormat nf = new DecimalFormat("###.##%");
        
        GL11.glOrtho(0, WIDTH, HEIGHT, 0, 1, -1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        
        while (!GLFW.glfwWindowShouldClose(window))
        {
            if (System.currentTimeMillis() > fpstimer + 1000)
            {
                fpstimer = System.currentTimeMillis();
                System.out.format("fps: %d  ups: %d%n", fps, ups);
                System.out.format("cities: %d %npercent done: %s%n", solver.getCities().size(), nf.format(solver.getPercent()));
                fps = ups = 0;
            }
            
            loops = 0;
            while (System.currentTimeMillis() > timer && loops < maxFramesSkipped)
            {
                update();
                timer += skipInterval;
                loops++;
                ups++;
                
            }
            render();
            fps++;
            
            synchronized (lock)
            {
                shouldClose = GLFW.glfwWindowShouldClose(window);
                if (!shouldClose)
                {
                    GLFW.glfwSwapBuffers(window);
                }
            }
        }
    }
    
    private void initPostGL()
    {
        int totalCities = 12;
        for (int i = 0; i < totalCities; i++)
        {
            cities.add(new Vector2f(random.nextInt(WIDTH), random.nextInt(HEIGHT)));
        }
        
        solver = new LexiographicSolver(cities, WIDTH, HEIGHT);
    }
    
    private void update()
    {
        if (solver.getCities().size() > 2)
        {
            solver.nextStep();
        }
    }
    
    private void render()
    {
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        
        GL11.glColor3f(1, 0, 0);
        Shapes.drawLine(new Vector2f(0, HEIGHT / 2), new Vector2f(WIDTH, HEIGHT / 2), 3);
        
        solver.render();
    }
    
}
