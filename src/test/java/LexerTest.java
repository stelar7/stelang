import ast.SyntaxTree;
import lexer.*;
import org.junit.Test;

import java.io.InputStream;
import java.util.*;

public class LexerTest
{
    public static String readInternalAsString(String filename)
    {
        InputStream   file   = Lexer.class.getClassLoader().getResourceAsStream(filename);
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
    
    public static void main(String[] args)
    {
        String data = readInternalAsString("test.st7");
        
        Lexer       lexer  = new Lexer();
        List<Token> tokens = lexer.parse(data);
        
        SyntaxTree syntaxTree = new SyntaxTree(tokens);
    }
}
