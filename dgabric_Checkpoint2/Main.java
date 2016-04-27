/*
Name: Daniel Gabric
ID: 0837154
*/
import java.util.*;
import java.io.*;

public class Main{
    public static void main(String []args){
        String fileName="";
        boolean isAbstract=false,isSymbol = false;
        if(args.length > 3){
            System.out.println("Error, too many arguments!");
            System.exit(0);
        }
        for(int i=0;i<args.length;++i){
            if(args[i].equals("-a"))isAbstract=true;
            else if(args[i].equals("-s"))isSymbol=true;
            else fileName = args[i];
        }
        if(fileName.equals("")||!fileName.substring(fileName.length()-3,fileName.length()).equals(".cm")){
            System.out.println("Error, c minus file not provided!");
            System.exit(0);
        }
        try {
            parser p = new parser(new Lexer(new FileReader(fileName)));
            p.isAbstract = isAbstract;
            p.isSymbol = isSymbol;
            Object result = p.parse().value;      
        } catch (Exception e) {
      
            e.printStackTrace();
        }
    }
}