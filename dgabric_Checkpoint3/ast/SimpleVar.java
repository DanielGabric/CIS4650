package ast;
import type.*;

public class SimpleVar extends Var{
    public int pos;
    public String name;

    public int size=-1;

    public VarType t=null;

    public SimpleVar(int pos, String name){
        this.pos = pos;
        this.name = name;
    }
}