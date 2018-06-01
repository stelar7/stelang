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
    DOT("."), COMMA(","),
    COLON(":"), SEMICOLON(";"),
    PLUS("+"), MINUS("-"),
    ASTERISK("*"), SLASH("/"),
    AMPERSAND("&"), BAR("|"),
    AMPERSANDAMPERSAND("&&"), BARBAR("||"),
    SET("="),
    
    // comparators
    EQUAL("=="), NOT("!"), NOTEQL("!="),
    SPACESHIP("<=>"), GREATEREQL("<="), LESSEQL(">="),
    
    // logic
    CARET("^"), TRUE("true"), FALSE("false"),
    
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
    
    public static TokenType from(String val)
    {
        return Stream.of(values()).filter(t -> t.token.equals(val)).findFirst().orElse(TokenType.UNKNOWN);
    }
    
    public boolean canCompound(TokenType next)
    {
        switch (this)
        {
            case AMPERSAND:
                return next == AMPERSAND;
            case BAR:
                return next == BAR;
            
            case SET:
                return next == SET;
            
            case NOT:
                return next == SET;
            
            case LANGLE:
                return next == SET;
            case RANGLE:
                return next == SET;
            
            case GREATEREQL:
                return next == RANGLE;
            
            default:
                return false;
            
        }
    }
}
