package lexer;

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
    AMPERSANDAMPERSAND("&&"), BARBAR("||"),
    SET("="), QUESTIONMARK("?"),
    
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
    CLASS("class"), ENUM("enum"), FUNCTION("function"), OPERATOR("operator"),
    
    IMPORT("import"), FROM("from"),
    
    // control
    IF("if"), ELSE("else"),
    CONTINUE("continue"), BREAK("break"),
    WHILE("while"), DO("do"),
    FOR("for"), THEN("then"),
    SWITCH("switch"), CASE("case"), DEFAULT("default"),
    RETURN("return"),
    
    // modifiers
    CONST("const"), VAL("val"),
    GLOBAL("global"), PURE("pure"),
    
    // lexing
    IDENTIFIER(""), KEYWORD(""), NUMBER(""),
    COMMENT(""), UNKNOWN("");
    
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
}
