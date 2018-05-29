import org.junit.Test;

public class LexerTest
{
    @Test
    public void testNumbers()
    {
        Lexer l = new Lexer();
        
        // these should be ok
        l.parse("1");
        l.parse("1_000");
        l.parse("9_223_372_036_854_775_807");
        l.parse("-9_223_372_036_854_775_807");
        l.parse("0xFF");
        l.parse("0b1");
        l.parse("0.1");
        
        // these should error
        l.parse("9_223_372_036_854_775_808");
        l.parse("-9_223_372_036_854_775_808");
    }
    
    @Test
    public void testIdentifiers()
    {
        Lexer l = new Lexer();
        
        // these should be ok
        l.parse("a");
        l.parse("abc");
        l.parse("camelCase");
        l.parse("snake_case");
        l.parse("TitleCase");
        l.parse("ALL_CAPS");
        l.parse("withNumber99");
        l.parse("_underscore");
        l.parse("Español");
        l.parse("日本語");
        l.parse("عربى");
        
        
        // these should error
        l.parse("_");
        l.parse("84a");
    }
    
    @Test
    public void testVariables()
    {
        Lexer l = new Lexer();
        
        // these should be ok
        l.parse("var a = 1");
        l.parse("const a = 1");
        l.parse("int a = 1");
        l.parse("var a = 1; a++");
        l.parse("const a = 1; a++");
        l.parse("int a = 1; a++");
        
        l.parse("int[] a = [0, 1, 2, 3]");
        l.parse("int[] a = 0..3");
        l.parse("int[] a = ..3");
        
        // these should error
    }
    
    @Test
    public void testComparison()
    {
        Lexer l = new Lexer();
        
        // these should be ok
        l.parse("1 < 2");
        l.parse("1 > 2");
        l.parse("1 = 2");
        l.parse("2 = 2");
        l.parse("[2] = [2]");
        l.parse("..3 = ..3");
        l.parse("1..3 = 1..3");
        l.parse("..3 = [0, 1, 2, 3]");
    }
    
    @Test
    public void testExampleClass()
    {
        Lexer l = new Lexer();
        
        l.parse("class A {\n" +
                "    const five = 5;\n" +
                "\n" +
                "    function add(Int: a, Int: b):Int {\n" +
                "        return a + b;\n" +
                "    }\n" +
                "    \n" +
                "    global function add2ToFive(Int: a):Int {\n" +
                "        return five + 2;\n" +
                "    }\n" +
                "    \n" +
                "    pure function add3(Int: a):Int {\n" +
                "        return a + 3;\n" +
                "    } \n" +
                "}\n" +
                "\n" +
                "class B {\n" +
                "    A.add2ToFive(2);\n" +
                "    A.add3(2);\n" +
                "}");
    }
    
    
    @Test
    public void testForLoop()
    {
        Lexer l = new Lexer();
        
        l.parse("for (val i = 0; i < 100; i++) \n" +
                "{\n" +
                "    // some stuff\n" +
                "}");
    }
    
    @Test
    public void testForEach()
    {
        Lexer l = new Lexer();
        
        l.parse("for (Text c : \"Some text\") \n" +
                "{\n" +
                "    // some stuff\n" +
                "}");
    }
    
    @Test
    public void testWhileLoop()
    {
        Lexer l = new Lexer();
        
        l.parse("val i = 0; " +
                "val b = 5; " +
                "while(i++ < b) {}");
    }
    
    @Test
    public void testLoopThen()
    {
        Lexer l = new Lexer();
        
        l.parse("for (val i = 0; i < 100; i++) \n" +
                "{\n" +
                "    // some stuff\n" +
                "} then {" +
                "   // some stuff" +
                "}");
    }
    
    @Test
    public void testLoopBreak()
    {
        Lexer l = new Lexer();
        
        l.parse("for (val i = 0; i < 100; i++) \n" +
                "{\n" +
                "    if (i > 50) break;\n" +
                "}");
    }
    
    @Test
    public void testLoopContinue()
    {
        Lexer l = new Lexer();
        
        l.parse("for (val i = 0; i < 100; i++) \n" +
                "{\n" +
                "    if (i > 50) continue;\n" +
                "}");
    }
    
    @Test
    public void testSwitch()
    {
        Lexer l = new Lexer();
        
        l.parse("val a = \"test\";\n" +
                "switch(a) {\n" +
                "    case \"test\": {\n" +
                "        // code stuff\n" +
                "    };" +
                "}");
    }
    
    @Test
    public void testSwitchSet()
    {
        Lexer l = new Lexer();
        
        l.parse("val b = switch(a) {\n" +
                "    case \"test\": {\n" +
                "        return \"b value\";\n" +
                "    };\n" +
                "}\n");
    }
    
    @Test
    public void testSwitchMultiMatch()
    {
        Lexer l = new Lexer();
        
        l.parse("val c = ..3;\n" +
                "switch(c) {\n" +
                "    case ..3: {\n" +
                "        // this matches because its the same value\n" +
                "    };\n" +
                "    \n" +
                "    case [0, 1, 2, 3]: {\n" +
                "        // this matches because its the same value\n" +
                "    };\n" +
                "}");
    }
    
    @Test
    public void testSwitchOrMatch()
    {
        Lexer l = new Lexer();
        
        l.parse("val c = ..3;\n" +
                "switch (c) {\n" +
                "\n" +
                "    // Int or \"testy\"\n" +
                "    case {Int, \"testy\"}: {\n" +
                "        Console.output(a);\n" +
                "    };\n" +
                "    \n" +
                "    // Int or Text with the value \"test\"\n" +
                "    case {Float, \"test\"}: {\n" +
                "        Console.output(a);\n" +
                "    };\n" +
                "}");
    }
}
