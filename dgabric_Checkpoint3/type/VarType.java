package type;
public class VarType extends Type{
    public int type;
    public int offset;
    public int nestLevel;

    public VarType(int type, int offset, int nestLevel){
        this.type = type;
        this.offset = offset;
        this.nestLevel = nestLevel;
    }
}