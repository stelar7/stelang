package div;

import java.io.InputStream;
import java.util.*;

public class Utils
{
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
