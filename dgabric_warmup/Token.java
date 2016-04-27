/**
 * @author Daniel Gabric
 * @studentid
 * @Title Token
 * @Date:Wednesday, February 4, 2016
 * Description: Token Class for JFlex
 */
public class Token{


    //Representations of all Tokens
    public final static int OPEN          = 0;
    public final static int CLOSE         = 1;
    public final static int WORD          = 2;
    public final static int NUMBER        = 3;
    public final static int APOSTROPHIZED = 4;
    public final static int HYPHENATED    = 5;
    public final static int PUNCTUATED    = 6;
    public final static int ERROR         = 7;

    public String value;
    public int type;
    public int relevant;

    public Token(int type, String value, int relevant){
        this.type = type;
        this.value = value;
        this.relevant = relevant;

    }

    /**
     *Returns the token type with its normalized name
     */
    public String toString(){
        if(type == OPEN){
            return "OPEN-"+value.toUpperCase();
        }else if(type == CLOSE){
            return "CLOSE-"+value.toUpperCase();
        }else if(type == WORD){
            return "WORD("+value+")";
        }else if(type == NUMBER){
            return "NUMBER("+value+")";
        }else if(type == APOSTROPHIZED){
            return "APOSTROPHIZED("+value+")";
        }else if(type == HYPHENATED){
            return "HYPHENATED("+value+")";
        }else if(type == PUNCTUATED){
            return "PUNCTUATION("+value+")";
        }else{
            return "ERROR("+value+")";
        }
    }
}