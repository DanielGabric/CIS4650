/*
Name: Daniel Gabric
ID: 
*/
import java.util.*;
import ast.*;
import type.*;

public class Semantic{

    public ArrayList<Hashtable<String,Type>> symbol;
    public DecList tree;
    public String output="The Symbol Table is:\n";
    public boolean isFunc = false;
    public int currentType = 0;
    public boolean hasReturn = false;
    public int position = 0;

    public int globalOffset=0;


    public int offset = 0;

    public int register=0;

    public int frameOffset = 0;
    public boolean isParam=false;


    public String spacing(int level){
        String spacing1="";

        for(int i=0;i<level*4;++i)
            spacing1+=" ";
        return spacing1;
    }

    public boolean checkMain(Dec bottom){
        if(bottom instanceof FunctionDec){
            FunctionDec f = (FunctionDec)bottom;

            if(f.result.typ == NameTy.VOID && f.func.equals("main")&&f.params==null){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public Semantic(DecList tree, boolean showTable){
        symbol = new ArrayList<Hashtable<String,Type>>();
        this.tree = tree;

        if(tree.size()>0){
            symbol.add(new Hashtable<String,Type>());

            String input = "input";
            String output = "output";

            ArrayList<Type> typOut = new ArrayList<Type>();
            ArrayList<Type> typIn = new ArrayList<Type>();
            typOut.add(new VarType(Type.INT,0,0));
            Type inputType = new FunctionType(Type.INT, typIn,null);
            Type outputType = new FunctionType(Type.VOID, typOut,null);


            symbol.get(0).put(output,outputType);
            symbol.get(0).put(input,inputType);

            if(!checkMain(tree.get(tree.size()-1)))
                System.err.println("Error: void main(void) is not the last declaration");
           
            this.output+="\nEntering scope level 0:\n";
            //addInputOutput();

            checkSemantics(tree,0, false);
            tree.globalOffset=globalOffset;
            this.output +="Exiting scope level 0:\n";
        }else{
            System.err.println("Error: No Declarations.");
            System.exit(0);
        }

        if(showTable)
            System.err.print(output);
    }

    public void checkSemantics(Var head, int level, boolean needInt){
        if( head instanceof SimpleVar )
            checkSemantics( (SimpleVar)head, level, needInt );
        else if( head instanceof IndexVar )
            checkSemantics( (IndexVar)head, level, needInt );
    }

    public void checkSemantics(Exp head, int level, boolean needInt){
        if( head instanceof AssignExp )
            checkSemantics( (AssignExp)head, level, needInt );
        else if( head instanceof CallExp )
            checkSemantics( (CallExp)head, level, needInt );
        else if( head instanceof CompoundExp )
            checkSemantics( (CompoundExp)head, level+1, needInt );
        else if( head instanceof IfExp )
            checkSemantics( (IfExp)head, level, needInt );
        else if( head instanceof IntExp )
            checkSemantics( (IntExp)head, level, needInt );
        else if( head instanceof OpExp )
            checkSemantics( (OpExp)head, level, needInt );
        else if( head instanceof WhileExp )
            checkSemantics( (WhileExp)head, level, needInt );
        else if( head instanceof VarExp )
            checkSemantics( (VarExp)head, level, needInt );
        else if( head instanceof ReturnExp ) {
            hasReturn = true;
            checkSemantics( (ReturnExp)head, level, needInt );
        }
    }

    public void checkSemantics(Dec head, int level, boolean needInt){
        if(head instanceof VarDec)
            checkSemantics((VarDec)head,level, needInt);
        else if(head instanceof FunctionDec)
            checkSemantics((FunctionDec)head,level, needInt);
    }

    public void checkSemantics(VarDecList tree, int level, boolean needInt){
        while( tree != null ) {
            if(tree.head!=null)
                checkSemantics( tree.head, level, needInt );
            tree = tree.tail;
        } 
    }

    public void checkSemantics(ExpList tree, int level, boolean needInt){
        
        while( tree != null ) {
            if(tree.head!=null)
                checkSemantics( tree.head, level, needInt );
            tree = tree.tail;
        } 
       
    }

    public void checkSemantics(VarDec tree, int level, boolean needInt){
        if(tree instanceof SimpleDec){
            checkSemantics((SimpleDec)tree,level, needInt);
        }else if(tree instanceof ArrayDec){
            checkSemantics((ArrayDec)tree,level, needInt);
        }
    }

    public void checkSemantics(SimpleVar tree, int level, boolean needInt){
        for(int i=level;i>=0;--i){
            if(symbol.get(i).containsKey(tree.name)){
                Type t = symbol.get(i).get(tree.name);
                if(!(t instanceof VarType))
                    System.err.println("Cannot convert array to int "+tree.name+" on line "+tree.pos);
                else{

                    tree.offset = ((VarType)t).offset;
                    tree.nestLevel = ((VarType)t).nestLevel;
                    tree.t=((VarType)t);

                }
                return;
            }
        }
        System.err.println("Undefined Variable "+tree.name+" on line "+tree.pos);
    }

    //FINISH THIS
    public void checkSemantics(IndexVar tree, int level, boolean needInt){

        boolean flag = false;
        Type t=null;

        for(int i=level;i>=0;--i){
            if(symbol.get(i).containsKey(tree.name)){
                t = symbol.get(i).get(tree.name);
                flag=true;
                break;
            }
        }

        if(flag){
            if(!(t instanceof ArrayType)){
                System.err.println("Error, "+tree.name+" defined as variable but referenced as array on line "+tree.pos);
            }else{
                tree.offset = ((ArrayType)t).offset;
                tree.nestLevel = ((ArrayType)t).nestLevel;
                tree.size = ((ArrayType)t).size;

            }

        }else{
            System.err.println("Undefined array "+tree.name+" on line "+tree.pos);
        }

        checkSemantics(tree.index,level, true);
    }

    public void checkSemantics(SimpleDec tree, int level, boolean needInt){
        if(symbol.get(level).containsKey(tree.name)){
            System.err.println("Redefined Variable "+tree.name+" on line "+tree.pos);
            return;
        }

        tree.offset = offset++;
        tree.nestLevel = level;
        if(level==0)globalOffset++;
        

        Type t = new VarType(Type.INT,offset-1,level);

        if(tree.typ.typ != NameTy.INT){
            //ROOM TO CHANGE
            System.err.println("Illegal declaration type: void "+tree.name+" on line "+tree.pos);
        }
        //ADD TO HASHTABLE

        symbol.get(level).put(tree.name, t);
    }

    public void checkSemantics(ArrayDec tree, int level, boolean needInt){
        if(symbol.get(level).containsKey(tree.name)){
            System.err.println("Redefined Variable "+tree.name+" on line "+tree.pos);
            return;
        }

        int size;
        if(tree.size == null)size=0;
        else size = Integer.parseInt(tree.size.value);

        tree.offset = offset;
        if(isParam)offset+=2;
        else offset+=size;
        tree.nestLevel = level;
        if(level==0)globalOffset+=size;

        //subject to change
        Type t = new ArrayType(Type.INT, size,tree.offset,tree.nestLevel);

        if(tree.typ.typ != NameTy.INT){
            System.err.println("Illegal array declaration type: void "+tree.name+" on line "+tree.pos);
        }

        symbol.get(level).put(tree.name,t);
    }

    //need to check elements
    //need to add parameter to check that return type of function is int
    public void checkSemantics(CallExp tree, int level, boolean needInt){
        if(!symbol.get(0).containsKey(tree.func)){
            System.err.println("Undefined function call "+tree.func+" on line "+tree.pos);
            return;
        }

        

        if(symbol.get(0).get(tree.func) instanceof FunctionType){
            //Check types of parameters vs function parameters
            FunctionType f = (FunctionType)symbol.get(0).get(tree.func);
            if(f.params.size()!=tree.size()){
                System.err.println("Calling function "+tree.func+" with too few or too many parameters on line "+tree.pos);
                return;
            }
            ExpList exp = tree.args;
            out:
            for(int i = 0;i < f.params.size();++i){
                Type t = f.params.get(i);
                Exp top = exp.head;
                if(t instanceof ArrayType){
                    if((top instanceof VarExp) && (((VarExp)top).name instanceof SimpleVar)){
                        String n = ((SimpleVar)((VarExp)top).name).name;
                        for(int j=level;j>=0;--j){
                            if(symbol.get(j).containsKey(n)){
                                if(!(symbol.get(j).get(n) instanceof ArrayType)){
                                    System.err.println("Expecting int array, got int variable in call on line "+tree.pos);
                                    
                                }else{
                                    ((SimpleVar)((VarExp)top).name).size=((ArrayType)symbol.get(j).get(n)).size;
                                    ((SimpleVar)((VarExp)top).name).nestLevel=((ArrayType)symbol.get(j).get(n)).nestLevel;
                                    ((SimpleVar)((VarExp)top).name).offset=((ArrayType)symbol.get(j).get(n)).offset;
                                    tree.pointToFunc = f.point;
                                    
                                }
                                exp = exp.tail;
                                continue out;
                                //}
                            }
                        }
                        System.err.println("Undefined variable in call on line "+tree.pos);
                    }else{
                        System.err.println("Expecting int array, got something else in call on line "+tree.pos);
                    }

                }else if(t instanceof VarType){
  
                    checkSemantics(top,level,true);

                }
                exp = exp.tail;
            }
            
            tree.pointToFunc = f.point;
        }else{
            System.err.println("Calling variable "+tree.func+" as function on line "+tree.pos);
            return;
        }
        if(needInt){
            if(((FunctionType)symbol.get(0).get(tree.func)).type == Type.VOID){
                System.err.println("Error, "+tree.func+" is of type void, cannot be treated as int on line "+tree.pos);
            }
        }
    }

    public void checkSemantics(IfExp tree, int level, boolean needInt){
        checkSemantics(tree.test,level,true);
        //PRINT SCOPES
        //symbol.add(new Hashtable<String, Type>());
        checkSemantics(tree.thenpart,level,needInt);
        //printRecentScope(level+1);

        //symbol.add(new Hashtable<String, Type>());
        checkSemantics(tree.elsepart,level,needInt);
        //printRecentScope(level+1);
    }

    public void checkSemantics(IntExp tree, int level, boolean needInt){
        //check for negative exponent?
    }

    public void checkSemantics(VarExp tree, int level, boolean needInt){
        checkSemantics(tree.name,level,needInt);
    }

    public void checkSemantics(ReturnExp tree, int level, boolean needInt){
        if(currentType == Type.INT){
            if(tree.exp == null){
                System.err.println("Returning nothing from a non-void function on line "+tree.pos);
            }else{
                checkSemantics(tree.exp, level, true);
            }
        }else{
            if(tree.exp != null){
                System.err.println("Returning something from a void function on line "+tree.pos);
            }
        }
        
    }

    //MAKE SURE EXPRESSION IS INTEGER
    public void checkSemantics(AssignExp tree, int level, boolean needInt){
        checkSemantics(tree.lhs,level,needInt);
        checkSemantics(tree.rhs,level,true);
    }

    public void checkSemantics(OpExp tree, int level, boolean needInt){
        checkSemantics(tree.left,level,true);
        checkSemantics(tree.right,level,true);
    }

    //MAKE SURE EXPRESSION IS INTEGER
    public void checkSemantics(WhileExp tree, int level, boolean needInt){
        checkSemantics(tree.test,level,true);

        //symbol.add(new Hashtable<String,Type>());
        checkSemantics(tree.body,level,needInt);

        //printRecentScope(level+1);
    }

    public void checkSemantics(CompoundExp exp, int level, boolean needInt){

        int saved=register;

        output+=spacing(level)+"Entering scope level "+level+":\n";
        if(symbol.size()-1!=level){
            symbol.add(new Hashtable<String,Type>());
        }
        checkSemantics(exp.decs,level, needInt);
        checkSemantics(exp.exps,level, needInt);
        
        printRecentScope(level);
        output+=spacing(level)+"Exiting scope level "+level+"\n";

        register = saved;

    }

    //FINISHED PROBABLY
    //assuming that void type parameters are actually int
    public void checkSemantics(FunctionDec fun, int level, boolean needInt){
        ArrayList<Type> parameters = new ArrayList<Type>();

        VarDecList v = fun.params;
        isFunc = true;
        hasReturn = false;
        position = fun.pos;
        //resetting offset for each function declaration
        offset = 2;

        String typ="";
        if(fun.result.typ == NameTy.INT)typ = "INT";
        else typ = "VOID";

        output += spacing(level)+"Function"+", "+fun.func+", "+typ+", Parameter Types: ";
        if(v==null){
            output+="VOID, ";
        }
        while(v != null){
            if(v.head instanceof ArrayDec){
                parameters.add(new ArrayType(Type.INT, 0,0,0));
                output += "INT_ARRAY, ";
            }else if(v.head instanceof SimpleDec){
                parameters.add(new VarType(Type.INT,0,0));
                output += "INT, ";
            }
            v = v.tail;
        }
        output = output.substring(0,output.length()-2)+"\n";
        //ADD PARAMETER LIST TYPES HERE

        FunctionType t = null;
        if(fun.result.typ == NameTy.INT){
            currentType = Type.INT;
            t = new FunctionType(Type.INT,parameters,fun);
        }else{
            currentType = Type.VOID;
            t = new FunctionType(Type.VOID,parameters,fun);
        }

        symbol.get(level).put(fun.func,t);
        symbol.add(new Hashtable<String,Type>());
        isParam=true;
        checkSemantics(fun.params,level+1,needInt);
        isParam=false;
        checkSemantics(fun.body,level,needInt);

        fun.frameOffset = offset;

        //printRecentScope(level+1);

        isFunc = false;
        if(NameTy.INT == fun.result.typ &&!hasReturn)
            System.err.println("No return in non-void function "+fun.func+" on line "+fun.pos);
        
    }

    //FINISHED
    public void checkSemantics(DecList head, int level, boolean needInt){
        for(int i=0;i<tree.size();++i){
            
            checkSemantics(tree.get(i),level, needInt);

        }
            
        printRecentScope(level);

    }


    //FINISHED
    public void printRecentScope(int level){
        Set<String> keys = symbol.get(level).keySet();
        boolean isT = false;

        for(String key: keys){
            isT = true;

            String typ="", var="";
            Type t = symbol.get(symbol.size()-1).get(key);


            if(t instanceof VarType){

                var = "Variable";
                if(((VarType)t).type == Type.INT){
                    typ = "INT";
                }else{
                    typ = "VOID";
                }
                output += spacing(level+1)+var+", "+key+", "+typ+"\n";
            }else if(t instanceof ArrayType){
                var = "Array";
                if(((ArrayType)t).type == Type.INT){
                    typ = "INT, Size: "+((ArrayType)t).size;
                }else{
                    typ = "VOID, Size: "+((ArrayType)t).size;
                }
                output += spacing(level+1)+var+", "+key+", "+typ+"\n";
            }else if(t instanceof FunctionType){
                var = "Function";
                if(((FunctionType)t).type == Type.INT){
                    typ = "INT";
                }else{
                    typ = "VOID";
                }
                ArrayList<Type> l = ((FunctionType)t).params;
                for(int i=0;i<l.size();++i){
                    Type t1 = l.get(i);
                    if(t1 instanceof VarType){
                        if(((VarType)t1).type == Type.INT){
                            var+=", INT";
                        }else{
                            var+=", VOID";
                        }
                        
                    }else{
                       
                        var+=", INT SIZE: "+((ArrayType)t1).size;
                        
                    }
                }
            }

            
        }
        //if(isT)output += "";
        symbol.remove(level);

    }

}