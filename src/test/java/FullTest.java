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
        String data = Utils.readFile("test3.st7");
        
        Lexer       lexer  = new Lexer();
        List<Token> tokens = lexer.parse(data);
        
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
