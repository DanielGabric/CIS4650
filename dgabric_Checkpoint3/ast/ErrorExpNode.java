package ast;

public class ErrorExpNode extends Exp{
    public int pos;
    public ErrorExpNode(int pos){
        this.pos = pos;
    }
}