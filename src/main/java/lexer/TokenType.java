package lexer;

import java.util.Arrays;
import java.util.stream.Stream;

public enum TokenType
{
    // braces
    LPAREN("("), RPAREN(")"),
    LBRACKET("["), RBRACKET("]"),
    LSQUIGLY("{"), RSQUIGLY("}"),
    LANGLE("<"), RANGLE(">"),
    
    // operators
    DOT("."), DOTDOT(".."), COMMA(","),
    COLON(":"), SEMICOLON(";"),
    PLUS("+"), MINUS("-"),
    ASTERISK("*"), SLASH("/"), PERCENT("%"),
    AMPERSAND("&"), BAR("|"),
    AMPERSANDAMPERSAND("&&"), BARBAR("||"), PLUSPLUS("++"), MINUSMINUS("--"),
    SET("="), QUESTIONMARK("?"), QUESTIONMARKCOLON("?:"), SQUIGLY("~"),
    
    SETEQL("==="), SETNOTEQL("=!="),
    SETRANGLE("=>"), SETRANGLEEQL("=>="),
    SETLANGLE("=<"), SETLANGLEEQL("=<="),
    SETSPACESHIP("=<=>"),
    SETPLUS("=+"), SETMINUS("=-"),
    SETASTERIX("=*"), SETSLASH("=/"), SETPERCENT("=%"),
    SETANDAND("=&&"), SETBARBAR("=||"), SETNOT("=!"),
    SETAND("=&"), SETBAR("=|"), SETCARET("=^"),
    SETRANGLERANGLE("=>>"), SETLANGLELANGLE("=<<"),
    
    // comparators
    EQUAL("=="), NOT("!"), NOTEQL("!="),
    SPACESHIP("<=>"), GREATEREQL("<="), LESSEQL(">="),
    ARROW("->"),
    
    // logic
    CARET("^"),
    TRUE("true"), FALSE("false"),
    LSHIFT("<<"), RSHIFT(">>"),
    
    // structure
    CLASS("class"), ENUM("enum"), FUNCTION("function"), CONSTRUCTOR("constructor"), OPERATOR("operator"), EXTENDS("extends"),
    
    IMPORT("import"), FROM("from"), DOUBLEQUOTE("\""), SINGLEQUOTE("'"),
    
    // control
    IF("if"), ELSE("else"),
    CONTINUE("continue"), BREAK("break"),
    WHILE("while"), DO("do"),
    FOR("for"), THEN("then"),
    SWITCH("switch"), CASE("case"), DEFAULT("default"),
    RETURN("return"), ASSERT("assert"),
    CAST("cast"), CREATE("create"),
    
    // modifiers
    CONST("const"), VAR("var"),
    
    // lexing
    IDENTIFIER(""), KEYWORD(""), NUMBER(""), FLOAT(""), TEXT(""),
    COMMENT(""), UNKNOWN(""), WHITESPACE("");
    
    String token;
    
    TokenType(String token)
    {
        this.token = token;
    }
    
    public static TokenType from(String val)
    {
        return Stream.of(values()).filter(t -> t.token.equals(val)).findFirst().orElse(TokenType.UNKNOWN);
    }
    
    public static boolean canCompound(String left, String right)
    {
        return from(left + right) != UNKNOWN;
    }
    
    public static boolean isSetType(Token currentToken)
    {
        return currentToken.getType().token.startsWith("=") && currentToken.getType() != EQUAL;
    }
    
    public static boolean isChainable(Token op)
    {
        return Arrays.asList(LANGLE, RANGLE, EQUAL, NOTEQL, GREATEREQL, LESSEQL).contains(op.getType());
    }
    
    public String getTokenChars()
    {
        return token;
    }
    
    @Override
    public String toString()
    {
        return this.name();
    }
    
    public TokenType toFirstToken()
    {
        if (this == PLUSPLUS)
        {
            return PLUS;
        }
        
        if (this == MINUSMINUS)
        {
            return MINUS;
        }
        
        return UNKNOWN;
    }
}
