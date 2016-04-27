import ast.*;
import java.util.*;
import java.io.*;
//during semantic analysis generate the variables for code generation
//temp variables generated within genCode, but pass in the amount of variables generated
//by semantic analysis
public class CodeGeneration{

    public DecList tree;
    public PrintWriter out;
    public int lineNumber=0;
    public int whichReg=0;


    public int spot=0;

    public int entry = 0;
    public int globalOffset = 0;
    public int frameOffset = 0;

    public CodeGeneration(DecList tree,String fileName){
        this.tree = tree;
        try{
            out = new PrintWriter(new File(fileName.substring(0,fileName.length()-3)+".tm"));
        }catch(IOException e){}
        out.println("0: LD  6,0(0)");
        out.println("1: LDA  5,0(6)");
        out.println("2: ST  0,0(0)");
        out.println("4: ST  0,-1(5)");
        out.println("5: IN  0,0,0");
        out.println("6: LD  7,-1(5)");
        out.println("7: ST  0,-1(5)");
        out.println("8: LD  0,-2(5)");
        out.println("9: OUT  0,0,0");
        out.println("10: LD  7,-1(5)");
        out.println("3: LDA  7,7(7)");
        
        lineNumber=11;
        globalOffset=tree.globalOffset;
        if(tree!=null)
            genCode(tree);

        out.println((lineNumber++)+": ST  5,"+(-globalOffset)+"(5)");
        out.println((lineNumber++)+": LDA  5,"+(-globalOffset)+"(5)");
        out.println((lineNumber++)+": LDA  0,1(7)");
        out.println((lineNumber++)+": LDA  7,"+(entry-lineNumber)+"(7)");
        out.println((lineNumber++)+": LD  5,0(5)");
        out.println((lineNumber++)+": HALT  0,0,0");
        out.close();
        

    }

    public void genCode(DecList tree){
        for(int i=0;i<tree.size();++i)
            genCode(tree.get(i));
    }

    public void genCode(Dec tree){
        if(tree instanceof FunctionDec)
            genCode((FunctionDec)tree);
    }

    public void genCode(ExpList tree){
        while(tree!=null){
            genCode(tree.head);
            tree=tree.tail;
        }
    }

    public void genCode(FunctionDec tree){
        spot=0;
        frameOffset = tree.frameOffset;

        tree.funAddr = lineNumber;
        if(tree.func.equals("main"))entry = lineNumber+1;

        int saveLine = lineNumber++;

        //load return address
        out.println((lineNumber++)+": ST 0,-1(5)");
        
        genCode(tree.body);

        //return to caller
        out.println((lineNumber++) +": LD 7,-1(5)");
        //back patching
        out.println(saveLine+": LDA 7,"+(lineNumber-saveLine-1)+"(7)");
    }



    public void genCode(Exp tree){
        if(tree instanceof AssignExp)
            genCode((AssignExp)tree);
        else if(tree instanceof CallExp)
            genCode((CallExp)tree);
        else if(tree instanceof CompoundExp)
            genCode((CompoundExp)tree);
        else if(tree instanceof IfExp)
            genCode((IfExp)tree);
        else if(tree instanceof OpExp){
            
            genCode((OpExp)tree);
        }else if(tree instanceof ReturnExp)
            genCode((ReturnExp)tree);
        else if(tree instanceof WhileExp)
            genCode((WhileExp)tree);
//        else if(tree instanceof VarExp)
//            genCode((VarExp)tree);
//        else if(tree instanceof IntExp)
//            genCode((IntExp)tree);

    }

    public void genCode(CallExp tree){

        if(tree.func.equals("output")){
            Exp exp = tree.args.head;
            if(exp instanceof VarExp){
                emitLoad(exp,0,false);
            }else if(exp instanceof IntExp){
                emitLoad(exp,0,false);
            }else if(exp instanceof OpExp){
                genCode(exp);
                out.println((lineNumber++)+": LD 0, "+(-exp.val-frameOffset)+"(5)");
            }else if(exp instanceof AssignExp){
                genCode(exp);
            }else{
                genCode(exp);
            }
            out.println((lineNumber++)+": ST  0, "+(-frameOffset - 2)+"(5)");
            out.println((lineNumber++)+": ST  5, "+(-frameOffset)+"(5)");
            out.println((lineNumber++)+": LDA  5, "+(-frameOffset)+"(5)");
            out.println((lineNumber++)+": LDA  0, 1(7)");
            out.println((lineNumber++)+": LDA  7, "+(7-lineNumber)+"(7)");
            out.println((lineNumber++)+": LD 5, 0(5)");

            


        }else if(tree.func.equals("input")){
            tree.val = spot++;
            out.println((lineNumber++)+": ST  5,"+(-frameOffset)+"(5)");
            out.println((lineNumber++)+": LDA  5,"+(-frameOffset)+"(5)");
            out.println((lineNumber++)+": LDA  0,1(7)");
            out.println((lineNumber++)+": LDA 7,"+(4-lineNumber)+"(7)");
            out.println((lineNumber++)+": LD 5,0(5)");
        }else{
           
            FunctionDec fd = tree.pointToFunc;
        
            int address = fd.funAddr;

            ExpList exp = tree.args;
            VarDecList vdl = fd.params;
            int oldOffset = frameOffset;
            int newOffset=2;

            while(exp != null){
                VarDec var = vdl.head;

                //spot=0;
                Exp exps = exp.head;
                if(exps instanceof VarExp){
                    if((((VarExp)exps).name) instanceof SimpleVar){

                        SimpleVar v = ((SimpleVar)(((VarExp)exps).name));

                        if(v.size!=-1){
                            if(v.size!=0){
                                out.println((lineNumber++)+": LDC 0, "+(v.size)+"(0)");
                                out.println((lineNumber++)+": ST 0, "+(-oldOffset-newOffset)+"(5) STORE ARG VAL");
                                
                            }else{

                                out.println((lineNumber++)+": LD 0, "+(-v.offset)+"(5)");
                                out.println((lineNumber++)+": ST 0, "+(-oldOffset-newOffset)+"(5) STORE ARG VAL2");
                                

                            }
                            newOffset++;
                        }

                    }
                    
                    emitLoad(exps,0,true);
                }else if(exps instanceof IntExp){
                    emitLoad(exps,0,true);
                }else{
                    genCode(exps);
                    out.println((lineNumber++)+": LD 0, "+(-exps.val-oldOffset-newOffset)+"(5)");
                }
                

                out.println((lineNumber++)+": ST 0, "+(-oldOffset-newOffset)+"(5) STORE ARG VAL");
                newOffset++;
                frameOffset=oldOffset+newOffset;
                
                exp = exp.tail;
                vdl = vdl.tail;
            }
            
            frameOffset = oldOffset;

            out.println((lineNumber++)+": ST  5, "+(-frameOffset)+"(5)");
            out.println((lineNumber++)+": LDA  5, "+(-frameOffset)+"(5)");
            out.println((lineNumber++)+": LDA  0,1(7)");
            out.println((lineNumber++)+": LDA  7,"+(address-lineNumber+1)+"(7)");
            out.println((lineNumber++)+": LD  5,0(5)");

            //frameOffset = fd.frameOffset;
        }

    }

    public void genCode(IfExp tree){
        genCode(tree.test);

        int saved = lineNumber++;

        genCode(tree.thenpart);
        out.println(saved+": JEQ 0, "+(lineNumber-saved)+"(7)");
        int saved1=lineNumber++;
        
        genCode(tree.elsepart);
        out.println(saved1+": LDA 7, "+(lineNumber-saved1-1)+"(7)");

    }

    public void genCode(WhileExp tree){
        int saved=lineNumber;
        genCode(tree.test);
        int saved1=lineNumber++;
        genCode(tree.body);

        //might be wrong by 1 just check
        out.println((lineNumber++)+": LDA 7, "+(saved-lineNumber)+"(7)");
        //may be wrong by 1
        out.println(saved1+": JEQ 0, "+(lineNumber-saved1-1)+"(7)");
    }

    public void genCode(ReturnExp tree){
        
        //save exp in register 0 somewhere 
        Exp exp = tree.exp;
        if(exp != null){
            if(exp instanceof VarExp || exp instanceof IntExp){
                emitLoad(exp,0,false);
            } else if(exp instanceof AssignExp || exp instanceof OpExp){
                genCode(exp);
                out.println((lineNumber++)+": LD 0, "+(-tree.exp.val-frameOffset)+"(5)");
            }else{
                genCode(exp);
            }

        }

        out.println((lineNumber++)+": LD 7, -1(5)");

        //do some return st00fs
    }

    public void genCode(CompoundExp tree){
        genCode(tree.exps);
    }

    public void emitOp(int op){
         switch( op ) {
            case OpExp.PLUS:
                out.println((lineNumber++)+": ADD 0,0,1");
                break;
            case OpExp.MINUS:
                out.println((lineNumber++)+": SUB 0,0,1");
                break;
            case OpExp.TIMES:
                out.println((lineNumber++)+": MUL 0,0,1");
                break;
            case OpExp.OVER:
                out.println((lineNumber++)+": DIV 0,0,1");
                break;
            case OpExp.LT:
                out.println((lineNumber++)+": SUB 0,0,1");
                out.println(((lineNumber++))+": JGE 0, 2(7)");
                out.println(((lineNumber++))+": LDC 0, 1(0)");
                out.println(((lineNumber++))+": LDA 7, 1(7)");
                out.println(((lineNumber++))+": LDC 0, 0(0)");
                break;
            case OpExp.GT:
                out.println((lineNumber++)+": SUB 0,0,1");
                out.println(((lineNumber++))+": JLE 0, 2(7)");
                out.println(((lineNumber++))+": LDC 0, 1(0)");
                out.println(((lineNumber++))+": LDA 7, 1(7)");
                out.println(((lineNumber++))+": LDC 0, 0(0)");
                break;
            case OpExp.EQUIVALENT:
                out.println((lineNumber++)+": SUB 0,0,1");
                out.println(((lineNumber++))+": JNE 0, 2(7)");
                out.println(((lineNumber++))+": LDC 0, 1(0)");
                out.println(((lineNumber++))+": LDA 7, 1(7)");
                out.println(((lineNumber++))+": LDC 0, 0(0)");
                break;
            case OpExp.LEQ:
                out.println((lineNumber++)+": SUB 0,0,1");
                out.println(((lineNumber++))+": JGT 0, 2(7)");
                out.println(((lineNumber++))+": LDC 0, 1(0)");
                out.println(((lineNumber++))+": LDA 7, 1(7)");
                out.println(((lineNumber++))+": LDC 0, 0(0)");
                break;
            case OpExp.GEQ:
                out.println((lineNumber++)+": SUB 0,0,1");
                out.println(((lineNumber++))+": JLT 0, 2(7)");
                out.println(((lineNumber++))+": LDC 0, 1(0)");
                out.println(((lineNumber++))+": LDA 7, 1(7)");
                out.println(((lineNumber++))+": LDC 0, 0(0)");
                break;
            case OpExp.NEQ:
                out.println((lineNumber++)+": SUB 0,0,1");
                out.println(((lineNumber++))+": JEQ 0, 2(7)");
                out.println(((lineNumber++))+": LDC 0, 1(0)");
                out.println(((lineNumber++))+": LDA 7, 1(7)");
                out.println(((lineNumber++))+": LDC 0, 0(0)");
                break;

        }
    }

    public void emitLoad(Exp tree, int register, boolean isCall){
        if(tree instanceof IntExp){
            out.println((lineNumber++)+": LDC "+register+", "+Integer.parseInt(((IntExp)tree).value)+"(0)");
        }else if(tree instanceof VarExp|| tree instanceof AssignExp){
            Var v = null;
            if(tree instanceof VarExp)
                v = ((VarExp)tree).name;
            else
                v = ((AssignExp)tree).lhs;

            int whichToUse=0;
            int off=0;

            if(v.nestLevel==0){
                whichToUse=6;
            }else whichToUse=5;
            if(v instanceof SimpleVar ){
                SimpleVar sv = ((SimpleVar)v);

                if(sv.size!=-1){
                    if(sv.size!=0)
                        out.println((lineNumber++)+": LDA "+register+", "+(-v.offset)+"("+whichToUse+")");
                    else{
                        out.println((lineNumber++)+": LD "+register+", "+(-v.offset-1)+"("+whichToUse+")");
                    }



                }else{
        
                    out.println((lineNumber++)+": LD "+register+", "+(-v.offset)+"("+whichToUse+")");
                }
                
            }else if(v instanceof IndexVar){
                Exp iv = ((IndexVar)v).index;
                IndexVar arr = ((IndexVar)v);

                genCode(iv);

                
                if(iv instanceof VarExp || iv instanceof IntExp){
                    emitLoad(iv,2,false);

                }else{
                    genCode(iv);

                    //store index in register 2
                    out.println((lineNumber++)+": LD 2, "+(-iv.val-frameOffset)+"(5)");
                
                }

                if(arr.size==0){
                    out.println((lineNumber++)+": LD 3, "+(-arr.offset)+"(5)");
                   // out.println((lineNumber++)+": OUT 3,0,0");
                    checkIndex(v,2,3);
                    out.println((lineNumber++)+": LD 3, "+(-arr.offset-1)+"(5)");
                    out.println((lineNumber++)+": SUB 3,3,2");
                    out.println((lineNumber++)+": LD "+register+", 0(3)");
                    return;   
                }


                checkIndex(v,2,-1);

                //store global/frame pointer + index in register 2
                out.println((lineNumber++)+": SUB 2,"+whichToUse+",2");
                

                //store value in global/frame pointer + index + frame offset + identifier offset in register
                out.println((lineNumber++)+": LD "+register+", "+(-v.offset)+"(2)");


            }

        }else if(tree instanceof CallExp){
            out.println((lineNumber++)+": LDA "+register+",0(0)");
        }

    }

    public void checkIndex(Var v, int register, int register2){
        if(register2==-1){
            out.println((lineNumber++)+": JLT "+register+", 3(7)");
            out.println((lineNumber++)+": LDC 4, "+((IndexVar)v).size+"(0)");
            out.println((lineNumber++)+": SUB 4,"+register+",4");
            out.println((lineNumber++)+": JLT 4, 3(7)");
            out.println((lineNumber++)+": LDC 4,-1000(0)");
            out.println((lineNumber++)+": OUT 4,0,0");
            out.println((lineNumber++)+": HALT 0,0,0");
        }else{
           // out.println((lineNumber++)+": OUT "+register+",0,0");
           // out.println((lineNumber++)+": OUT "+register2+",0,0");
            out.println((lineNumber++)+": JLT "+register+", 3(7)");
            out.println((lineNumber++)+": SUB 4,"+register+","+register2);
            out.println((lineNumber++)+": JLT 4, 3(7)");
            out.println((lineNumber++)+": LDC 4,-1000(0)");
            out.println((lineNumber++)+": OUT 4,0,0");
            out.println((lineNumber++)+": HALT 0,0,0");
        }
        
    }

    //use 3 address codes
    //r0,r1,r2

    public void genCode(OpExp tree){
        genCode(tree.left);
        genCode(tree.right);

        tree.val = spot++;



        if(tree.left.val==-1 && tree.right.val==-1){

            emitLoad(tree.left,0,false);
            emitLoad(tree.right,1,false);

        }else if(tree.left.val==-1 && tree.right.val!=-1){
            int offset=-tree.right.val-frameOffset;
            out.println((lineNumber++)+": LD 1, "+offset+"(5)");
            
            emitLoad(tree.left,0,false);

        }else if(tree.left.val!=-1 && tree.right.val==-1){
            int offset=-tree.left.val-frameOffset;
            out.println((lineNumber++)+": LD 0, "+offset+"(5)");

            emitLoad(tree.right,1,false);



        }else{
            int offset1=-tree.left.val-frameOffset;
            int offset2=-tree.right.val-frameOffset;

            out.println((lineNumber++)+": LD 0, "+offset1+"(5)");
            out.println((lineNumber++)+": LD 1, "+offset2+"(5)");

            
        }

        emitOp(tree.op);

        out.println((lineNumber++)+": ST 0, "+(-tree.val-frameOffset)+"(5)");

    }

    public void genCode(AssignExp tree){
        Var v = tree.lhs;
        int off = v.offset;
        int nest = v.nestLevel;
        int whichToUse=0;

        if(v.nestLevel==0)whichToUse=6;
        else whichToUse=5;

        whichReg=0;

        genCode(tree.rhs);

        if(tree.rhs instanceof AssignExp){
            Var v1 = ((AssignExp)tree.rhs).lhs;
            emitLoad(new VarExp(0,v1),3,false);
        }else if(tree.rhs instanceof VarExp){
            emitLoad(tree.rhs,3,false);
        }else if(tree.rhs instanceof IntExp){
            emitLoad(tree.rhs,3,false);
        }else if(tree.rhs instanceof OpExp){
            out.println((lineNumber++)+": LD 3, "+(-tree.rhs.val-frameOffset)+"(5)");
        }else{
            out.println((lineNumber++)+": LDA 3,0(0)");
        }

        if(v instanceof SimpleVar){
            out.println((lineNumber++)+": ST 3, "+(-v.offset)+"("+whichToUse+")");
        }else if(v instanceof IndexVar){
            Exp iv = ((IndexVar)v).index;
            IndexVar arr = ((IndexVar)v);
            
            if(iv instanceof VarExp || iv instanceof IntExp){
                emitLoad(iv,2,false);

            }else{
                genCode(iv);

                out.println((lineNumber++)+": LD 2, "+(-iv.val-frameOffset)+"(5)");


            }
            if(arr.size==0){
                out.println((lineNumber++)+": LD 1, "+(-arr.offset)+"(5)");
                checkIndex(v,2,1);
                out.println((lineNumber++)+": LD 1, "+(-arr.offset-1)+"(5)");
                out.println((lineNumber++)+": SUB 1,1,2");
                out.println((lineNumber++)+": ST 3, 0(1)");
                return;
            }
            checkIndex(v,2,-1);
            out.println((lineNumber++)+": SUB 2,"+whichToUse+",2");
            out.println((lineNumber++)+": ST 3, "+(-v.offset)+"(2)");



        }
    }

    public void emitIndex(IndexVar iv, int register){

    }

    public static void main(String[]args){
       
    }

}