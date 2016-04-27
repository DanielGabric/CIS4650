package ast;

public class FunctionDec extends Dec{
    public int pos;
    public NameTy result;
    public String func;
    public VarDecList params;
    public Exp body;
    public FunctionDec(int pos, NameTy result, String func, VarDecList params, Exp body){
        this.pos = pos;
        this.result = result;
        this.func = func;
        this.params = params;
        this.body = body;
    }
}