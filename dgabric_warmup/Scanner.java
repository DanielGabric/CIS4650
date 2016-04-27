import java.util.*;
import java.io.*;
/**
 * @author Daniel Gabric
 * @studentid
 * @Title Scanner
 * @Date:Wednesday, February 4, 2016
 * Description: Scanner Class for Lexer, also analyzes syntax
 */
public class Scanner {
    private Lexer scanner = null;

    public Scanner( Lexer lexer ) {
        scanner = lexer; 
    }

    /**
     *Returns next token from Lexer, null if EOF read
     */
    public Token getNextToken() throws java.io.IOException {
        return scanner.yylex();
    }

    /**
     *Returns if the current scope is relevant or not
     */
    public static int isRelevant(ArrayList<Token> stack){
        if(stack.isEmpty()){
            return -1;
        }
        int c=0;
        for(Token t: stack){
            if(t.relevant == 0)
                return 0;
            if(t.relevant == -1)
                c++;
        }
        if(c==stack.size())
            return 0;
        return 1;
    }

    /**
     *Prints all of the currently open tags
     */
    public static void checkOpen(ArrayList<Token> stack){
        if(!stack.isEmpty()){
            System.out.println("\nError, there are unmatched open tags! They are listed as follows:");
            for(Token t:stack){
                System.out.println(t.toString());
            }
        }
    }

    /**
     *Prints the current tag only if it's within a relevant scope
     */
    public static void print(ArrayList<Token> stack,Token tok){
        int r = isRelevant(stack);
        if(r==1){
            System.out.println(tok.toString());
        }
    }

    public static void main(String argv[]) {
        try {
            Scanner scanner = new Scanner(new Lexer(System.in));
            Token tok = null;
            ArrayList<Token> stack = new ArrayList<Token>();
            String endNoMatch = "";
            //Taking tokens from Lexer and checking syntax
            while( (tok=scanner.getNextToken()) != null ){
                
                if(tok.type==Token.OPEN){
                    stack.add(tok);
                    print(stack,tok);
                }else if(tok.type == Token.CLOSE){
                    if(stack.isEmpty()){
                        System.out.println("ERROR, "+tok.value+" HAS NO OPEN TAG TO CHOOSE FROM!");
                    }else{
                        Token top = stack.get(stack.size()-1);
                        if((top.value).equals(tok.value)){
                            print(stack,tok);
                            stack.remove(stack.size()-1);
                        }else{
                            System.out.println("ERROR, "+tok.value+" HAS NO MATCHING OPEN TAG!");
                        }
                    }
                }else if(tok.type == Token.ERROR){
                    System.out.println(tok.toString());
                }else{
                    print(stack,tok);
                }
            }
            checkOpen(stack);
        }
        catch (Exception e) {
            System.out.println("Unexpected exception:");
            e.printStackTrace();
        }
    }
}
