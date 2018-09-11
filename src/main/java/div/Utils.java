package div;

import java.io.*;
import java.util.*;

public class Utils
{
    public static String readFileExternal(String filename)
    {
        StringBuilder result = new StringBuilder();
        try (Scanner scanner = new Scanner(new File(".\\stelang\\src\\main\\resources\\" + filename)))
        {
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return result.toString();
    }
    
    
    public static String readFile(String filename)
    {
        InputStream   file   = Utils.class.getClassLoader().getResourceAsStream(filename);
        StringBuilder result = new StringBuilder();
        
        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        }
        
        return result.toString();
    }
    
    
    public static List<String> readFolder(String filename)
    {
        InputStream  file      = Utils.class.getClassLoader().getResourceAsStream(filename);
        List<String> filenames = new ArrayList<>();
        if (file == null)
        {
            return filenames;
        }
        
        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                filenames.add(line);
            }
        }
        
        return filenames;
    }
}
