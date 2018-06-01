public enum TokenType
{
    // braces
    LPAREN("("), RPAREN(")"),
    LBRACKET("["), RBRACKET("]"),
    LSQUIGLY("{"), RSQUIGLY("}"),
    LANGLE("<"), RANGLE(">"),
    
    // operators
    DOT("."), COMMA(","),
    COLON(":"), SEMICOLON(";"),
    PLUS("+"), MINUS("-"),
    ASTERISK("*"), SLASH("/"),
    AMPERSAND("&"), BAR("|"),
    
    // comparators
    EQUAL("="), NOT("!"), GREATER("<"), LESS(">"),
    
    // logic
    XOR("^"), TRUE("true"), FALSE("false"),
    
    // structure
    CLASS("class"), ENUM("enum"), FUNCTION("function"),
    
    // control
    IF("if"), ELSE("else"),
    CONTINUE("continue"), BREAK("break"),
    WHILE("while"), DO("do"), FOR("for"), THEN("then"),
    SWITCH("switch"), CASE("case"),
    RETURN("return"),
    
    // modifiers
    CONST("const"), VAL("val"), GLOBAL("global"), PURE("pure"),
    
    // lexing
    IDENTIFIER(""), KEYWORD(""), COMMENT(""), UNKNOWN(""),
    
    // literal
    TEXT("text"), INT("int"), FLOAT("float"), NULL("null"), BOOL("bool");
    
    String token;
    
    TokenType(String token)
    {
        this.token = token;
    }
}
