package ast;

public class CallExp extends Exp{
    public int pos;
    public String func;
    public ExpList args;


    public FunctionDec pointToFunc;

    public CallExp(int pos, String func, ExpList args){
        this.pos = pos;
        this.func = func;
        this.args = args;
    }


    public int size(){
        int size=0;
        ExpList e = args;
        while(e != null){
            size++;
            e = e.tail;
        }
        return size;
    }
}