package type;

public class ArrayType extends Type{
    public int size;
    public int type;
    public int varPos;
    public int offset;
    public int nestLevel;

    public ArrayType(int type, int size, int offset, int nestLevel){
        this.type = type;
        this.size = size;
        this.offset = offset;
        this.nestLevel = nestLevel;

    }
}