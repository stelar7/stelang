package ast;

public class VariableDefinitionSyntax extends Syntax
{
    private String identifier;
    private String visibility;
    
    public VariableDefinitionSyntax(String identifier, String visibility)
    {
        this.identifier = identifier;
        this.visibility = visibility;
    }
    
}
