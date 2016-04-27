package ast;
import java.util.*;
public class DecList{
    public ArrayList<Dec> head;
    public DecList tail;

    public DecList(){
        head = new ArrayList<Dec>();
    }

    public int size(){
        return head.size();
    }

    public void add(Dec dec){
        head.add(dec);
    }

    public Dec get(int i){
        return head.get(i);
    }
}