import ast.*;
import div.Utils;
import lexer.*;
import semantic.SemanticParser;

import java.util.*;

public class LexerTest
{
    
    public static void main(String[] args)
    {
        String data = Utils.readFile("test.st7");
        
        Lexer       lexer  = new Lexer();
        List<Token> tokens = lexer.parse(data);
        
        SyntaxTree     syntaxTree = new SyntaxTree(tokens);
        SemanticParser semantics  = new SemanticParser(syntaxTree);
    }
}
