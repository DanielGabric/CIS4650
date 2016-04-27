package type;
import java.util.*;
import ast.*;
public class FunctionType extends Type{
    public int type;
    public ArrayList<Type> params;

    public FunctionDec point;

    public FunctionType(int type, ArrayList<Type> params, FunctionDec point){
        this.type = type;
        this.params = params;
        this.point = point;
    }
}