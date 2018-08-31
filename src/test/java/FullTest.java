import ast.*;
import ast.exprs.Expression;
import div.Utils;
import lexer.*;
import semantic.SemanticParser;

import java.util.*;

public class FullTest
{
    
    public static void main(String[] args)
    {
        String filename = "test.st7";
        String data = Utils.readFile(filename);
        
        Lexer       lexer  = new Lexer();
        List<Token> tokens = lexer.parse(filename, data);
        
        SyntaxTree     syntaxTree = new SyntaxTree(tokens);
        SemanticParser semantics  = new SemanticParser(syntaxTree);
        
        
        /*
        for (Expression expression : syntaxTree.getAST())
        {
            System.out.println(expression.codegen());
        }
        */
    }
}
