package ast;

public class IndexVar extends Var{
    public int pos;
    public String name;
    public Exp index;

    public IndexVar(int pos, String name, Exp index){
        this.pos = pos;
        this.name = name;
        this.index = index;
    }
}