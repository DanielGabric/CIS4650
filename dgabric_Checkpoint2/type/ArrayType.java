package type;

public class ArrayType extends Type{
    public int size;
    public int type;

    public ArrayType(int type, int size){
        this.type = type;
        this.size = size;
    }
}