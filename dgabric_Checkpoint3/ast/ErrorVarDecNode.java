package ast;

public class ErrorVarDecNode extends VarDec{
    public int pos;
    public ErrorVarDecNode(int pos){
        this.pos = pos;
    }
}