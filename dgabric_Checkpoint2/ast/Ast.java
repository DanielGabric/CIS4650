package ast;
import java.util.*;
abstract public class Ast {
    public int pos;

    final static int SPACES = 4;

    static private void indent( int spaces ) {
        for( int i = 0; i < spaces; i++ ) System.out.print( " " );
    }

    static public void showTree( ExpList tree, int spaces ) {
        while( tree != null ) {
            showTree( tree.head, spaces );
            tree = tree.tail;
        } 
    }

    static public void showTree( VarDecList tree, int spaces ) {
        while( tree != null ) {
            if(tree.head!=null)
                showTree( tree.head, spaces );
            tree = tree.tail;
        } 
    }

    static public void showTree( DecList tree, int spaces ) {
        for(int i=0;i<tree.size();++i){
            showTree( tree.get(i), spaces );
        } 
    }

    static private void showTree( Dec tree, int spaces ) {
        if( tree instanceof FunctionDec )
            showTree( (FunctionDec)tree, spaces );
        else if( tree instanceof VarDec )
            showTree( (VarDec)tree, spaces );
        else {
            indent( spaces );
            System.out.println( "Illegal expression at line " + ((ErrorDecNode)tree).pos  );
        }
    }  

    static private void showTree( Var tree, int spaces ) {
        if( tree instanceof SimpleVar )
            showTree( (SimpleVar)tree, spaces );
        else if( tree instanceof IndexVar )
            showTree( (IndexVar)tree, spaces );
        
    }  

    static private void showTree( VarDec tree, int spaces ) {
        if( tree instanceof ArrayDec )
            showTree( (ArrayDec)tree, spaces );
        else if( tree instanceof SimpleDec )
            showTree( (SimpleDec)tree, spaces );
        else {

            indent( spaces );
            System.out.println( "Illegal expression at line " + ((ErrorVarDecNode)tree).pos  );
        }
    }
    
    static private void showTree( Exp tree, int spaces ) {
        if( tree instanceof AssignExp )
            showTree( (AssignExp)tree, spaces );
        else if( tree instanceof CallExp )
            showTree( (CallExp)tree, spaces );
        else if( tree instanceof CompoundExp )
            showTree( (CompoundExp)tree, spaces );
        else if( tree instanceof IfExp )
            showTree( (IfExp)tree, spaces );
        else if( tree instanceof IntExp )
            showTree( (IntExp)tree, spaces );
        else if( tree instanceof OpExp )
            showTree( (OpExp)tree, spaces );
        else if( tree instanceof WhileExp )
            showTree( (WhileExp)tree, spaces );
        else if( tree instanceof VarExp )
            showTree( (VarExp)tree, spaces );
        else if( tree instanceof ReturnExp ) 
            showTree( (ReturnExp)tree, spaces );
        else {
            if( tree instanceof ErrorExpNode){
                indent( spaces );

                System.out.println( "Illegal expression at line " + ((ErrorExpNode)tree).pos  );
            }
        }
    }

    static private void showTree( NameTy tree, int spaces ) {
        indent( spaces );
        if(tree.typ == NameTy.VOID)
            System.out.println( "Type: VOID");
        else
            System.out.println( "Type: INT");
        
    }

    static private void showTree( FunctionDec tree, int spaces ) {
        indent( spaces );
        System.out.println( "FunctionDec:" );

        spaces += SPACES;

        showTree( tree.result, spaces );
        indent( spaces );
        System.out.println( "FunctionName: "+tree.func);
        showTree( tree.params, spaces );
        showTree( tree.body, spaces );
    }

    static private void showTree( SimpleVar tree, int spaces ) {
        indent( spaces );
        System.out.println( "SimpleVar: " +tree.name);
    }

    static private void showTree( IndexVar tree, int spaces ) {
        indent( spaces );
        System.out.println( "IndexVar:" );

        spaces += SPACES;

        indent( spaces );
        System.out.println( "Name: "+ tree.name);
        showTree(tree.index, spaces);
    }

    static private void showTree( SimpleDec tree, int spaces ) {
        indent( spaces );
        System.out.println( "SimpleDec:" );

        spaces += SPACES;

        showTree( tree.typ, spaces );
        indent( spaces );
        System.out.println( "Name: " + tree.name);
    }

    static private void showTree( ArrayDec tree, int spaces ) {
        indent( spaces );
        System.out.println( "ArrayDec:" );
        spaces += SPACES;
        showTree( tree.typ, spaces);
        indent( spaces );
        System.out.println( "Name: " + tree.name);
        showTree( tree.size, spaces);

    }

    static private void showTree( AssignExp tree, int spaces ) {
        indent( spaces );
        System.out.println( "AssignExp:" );
        spaces += SPACES;
        showTree( tree.lhs, spaces );
        showTree( tree.rhs, spaces );
    }

    static private void showTree( CallExp tree, int spaces ) {
        indent( spaces );
        System.out.println( "CallExp:" );

        spaces += SPACES;

        indent( spaces );
        System.out.println("FunctionName: "+tree.func);
        showTree(tree.args, spaces);
    }

    static private void showTree( CompoundExp tree, int spaces ) {
        indent( spaces );
        System.out.println( "CompoundExp:" );

        spaces += SPACES;

        showTree( tree.decs, spaces );
        showTree( tree.exps, spaces );
    }


    static private void showTree( IfExp tree, int spaces ) {
        indent( spaces );
        System.out.println( "IfExp:" );
        spaces += SPACES;
        showTree( tree.test, spaces );
        showTree( tree.thenpart, spaces );
        showTree( tree.elsepart, spaces );
    }

    static private void showTree( IntExp tree, int spaces ) {
        indent( spaces );
        System.out.println( "IntExp: " + tree.value ); 
    }

    static private void showTree( OpExp tree, int spaces ) {
        indent( spaces );
        System.out.print( "OpExp:" ); 
        switch( tree.op ) {
            case OpExp.PLUS:
                System.out.println( " + " );
                break;
            case OpExp.MINUS:
                System.out.println( " - " );
                break;
            case OpExp.TIMES:
                System.out.println( " * " );
                break;
            case OpExp.OVER:
                System.out.println( " / " );
                break;
            case OpExp.LT:
                System.out.println( " < " );
                break;
            case OpExp.GT:
                System.out.println( " > " );
                break;
            case OpExp.EQUIVALENT:
                System.out.println( " == " );
                break;
            case OpExp.LEQ:
                System.out.println( " <= " );
                break;
            case OpExp.GEQ:
                System.out.println( " >= " );
                break;
            case OpExp.NEQ:
                System.out.println( " != " );
                break;
            default:
                System.out.println( "Unrecognized operator at line " + tree.pos);
        }
        spaces += SPACES;
        showTree( tree.left, spaces );
        showTree( tree.right, spaces ); 
    }

    static private void showTree( WhileExp tree, int spaces ) {
        indent( spaces );
        System.out.println( "WhileExp:" );
        spaces += SPACES;
        showTree( tree.test, spaces );
        showTree( tree.body, spaces );
    }

    static private void showTree( ReturnExp tree, int spaces ) {
        indent( spaces );
        System.out.println( "ReturnExp:" );
        spaces += SPACES;
        if(tree.exp != null)
            showTree( tree.exp, spaces );
    }

    static private void showTree( VarExp tree, int spaces ) {
        indent( spaces );
        System.out.println( "VarExp: ");
        spaces += SPACES;
        showTree(tree.name,spaces);
    }


}