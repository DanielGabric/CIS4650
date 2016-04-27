/*
Name: Daniel Gabric
ID: 
*/
import java_cup.runtime.*;

%%

%class Lexer
%line
%column
%cup

/*At EOF return null*/
%eofval{
  return null;
%eofval}


%{   
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}



LineTerminator = \r|\n|\r\n
   
WhiteSpace     = {LineTerminator} | [ \t\f]

letter         = [a-zA-Z]
digit          = [0-9]
number         = {digit}+
id             = {letter}+
commentOpen    = "/*"
commentClose   = "*/"
comment        = {commentOpen}[^"*/"]*{commentClose}


%%


"if"               { return symbol(sym.IF); }
"else"             { return symbol(sym.ELSE); }
"while"            { return symbol(sym.WHILE); }
"void"             { return symbol(sym.VOID); }
"int"              { return symbol(sym.INT); }
"return"           { return symbol(sym.RETURN); }
"}"                { return symbol(sym.RBRACK); }
"{"                { return symbol(sym.LBRACK); }
")"                { return symbol(sym.RPAREN); }
"("                { return symbol(sym.LPAREN); }
"]"                { return symbol(sym.RSQUARE); }
"["                { return symbol(sym.LSQUARE); }
"="                { return symbol(sym.EQ); }
"=="               { return symbol(sym.EQUIVALENT); }
"<"                { return symbol(sym.LT); }
">"                { return symbol(sym.GT); }
">="               { return symbol(sym.GEQ); }
"<="               { return symbol(sym.LEQ); }
"!="               { return symbol(sym.NEQ); }
"+"                { return symbol(sym.PLUS); }
"-"                { return symbol(sym.MINUS); }
"*"                { return symbol(sym.TIMES); }
"/"                { return symbol(sym.OVER); }
"("                { return symbol(sym.LPAREN); }
")"                { return symbol(sym.RPAREN); }
";"                { return symbol(sym.SEMI); }
","                { return symbol(sym.COMMA); }
{number}           { return symbol(sym.NUM, yytext()); }
{id}               { return symbol(sym.ID, yytext()); }
{WhiteSpace}*      { /* skip whitespace */ }  
{comment}          { /*Do nothing on comments*/ } 
.                  { System.err.println("Error: Invalid character \'"+yytext()+"\' on line "+yyline+".");/*return symbol(sym.ERROR);*/ }


