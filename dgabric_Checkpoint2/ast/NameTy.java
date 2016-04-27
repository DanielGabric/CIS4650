package ast;

public class NameTy{
    public final static int VOID = 0;
    public final static int INT  = 1;
    public int pos;
    public int typ;
    public NameTy(int pos, int typ){
        this.pos=pos;
        this.typ=typ;
    }
}