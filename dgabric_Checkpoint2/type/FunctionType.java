package type;
import java.util.*;
public class FunctionType extends Type{
    public int type;
    public ArrayList<Type> params;
    public FunctionType(int type, ArrayList<Type> params){
        this.type = type;
        this.params = params;
    }
}