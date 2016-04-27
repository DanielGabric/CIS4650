%%

%class Lexer
%type Token
%line
%column

/*At EOF return null*/
%eofval{
  return null;
%eofval}

LineTerminator = \r|\n|\r\n
   
WhiteSpace     = {LineTerminator} | [ \t\f]

letter         = [a-zA-Z]
digit          = [0-9]
sign           = ("+"|"-")?
sigma          = ({letter}|{digit})*
sigma1         = ({letter}|{digit})+
word           = {sigma}{letter}{sigma}
apostrophized  = {sigma1}"'"{sigma1}("'"{sigma1})?
hyphenated     = {sigma1}"-"{sigma1}("-"{sigma1})?
realApo        = ({sigma1}"-"{apostrophized})|({sigma1}"-"{sigma1}"-"{apostrophized})
tag            = [a-zA-Z0-9"_"][a-zA-Z0-9"-""_"]*


%%


{WhiteSpace}                                                    {/*Do nothing on white space*/}
"<"{WhiteSpace}*{tag}{WhiteSpace}*[^"<"">"]*">"                 {/*OPEN TAGS*/
                                                                String text = yytext().substring(1);
                                                                String str = "";
                                                                int i;
                                                                for(i=0;i<text.length();++i){
                                                                    if(text.charAt(i)=='_'||text.charAt(i)=='-')
                                                                        break;
                                                                    if(text.charAt(i)>='a'&&text.charAt(i)<='z')
                                                                        break;
                                                                    if(text.charAt(i)>='A'&&text.charAt(i)<='Z')
                                                                        break;
                                                                    if(text.charAt(i)>='0'&&text.charAt(i)<='9')
                                                                        break;
                                                                }
                                                                for(;i<text.length();++i){
                                                                    if(text.charAt(i)<'a'||text.charAt(i)>'z')
                                                                        if(text.charAt(i)<'A'||text.charAt(i)>'Z')
                                                                            if(text.charAt(i)<'0'||text.charAt(i)>'9' || text.charAt(i)=='>')
                                                                                if(text.charAt(i)!='_' && text.charAt(i)!='-')
                                                                                    break;
                                                                    str += text.charAt(i);
                                                                }
                                                                str = str.toUpperCase().trim();
                                                                if(str.equals("P")){
                                                                    return new Token(Token.OPEN,str,-1);
                                                                }
                                                                if(str.equals("DOC")|| str.equals("TEXT")|| str.equals("DATE")|| str.equals("DOCNO")|| str.equals("HEADLINE")|| str.equals("LENGTH")){
                                                                    return new Token(Token.OPEN,str,1);
                                                                }
                                                                return new Token(Token.OPEN,str,0);
                                                                }
"</"{WhiteSpace}*{tag}{WhiteSpace}*">"                          {/*CLOSE TAGS*/
                                                                String text = yytext().substring(2);
                                                                String str = "";
                                                                int i;
                                                                for(i=0;i<text.length();++i){
                                                                    if(text.charAt(i)=='_'||text.charAt(i)=='-')
                                                                        break;
                                                                    if(text.charAt(i)>='a'&&text.charAt(i)<='z')
                                                                        break;
                                                                    if(text.charAt(i)>='A'&&text.charAt(i)<='Z')
                                                                        break;
                                                                    if(text.charAt(i)>='0'&&text.charAt(i)<='9')
                                                                        break;
                                                                }
                                                                for(;i<text.length();++i){
                                                                    if(text.charAt(i)<'a'||text.charAt(i)>'z')
                                                                        if(text.charAt(i)<'A'||text.charAt(i)>'Z')
                                                                            if(text.charAt(i)<'0'||text.charAt(i)>'9' || text.charAt(i)=='>')
                                                                                if(text.charAt(i)!='_' && text.charAt(i)!='-')
                                                                                    break;
                                                                    str += text.charAt(i);
                                                                }
                                                                str = str.toUpperCase().trim();
                                                                if(str.equals("P")){
                                                                    return new Token(Token.CLOSE,str,-1);
                                                                }
                                                                if(str.equals("DOC")|| str.equals("TEXT")|| str.equals("DATE")|| str.equals("DOCNO")|| str.equals("HEADLINE")|| str.equals("LENGTH")){
                                                                    return new Token(Token.CLOSE,str,1);
                                                                }
                                                                return new Token(Token.CLOSE,str,0);
                                                                }
{word}                                                          {/*WORD*/return new Token(Token.WORD,yytext(),-1);}
{sign}{digit}+                                                  {/*NUMBER*/return new Token(Token.NUMBER,yytext(),-1);}    
{sign}{digit}+"."{digit}*                                       {/*NUMBER*/return new Token(Token.NUMBER,yytext(),-1);} 
{sign}{digit}*"."{digit}+                                       {/*NUMBER*/return new Token(Token.NUMBER,yytext(),-1);} 
{apostrophized}                                                 {/*APOSTROPHIZED*/return new Token(Token.APOSTROPHIZED,yytext(),-1);}
{realApo}                                                       {/*APOSTROPHIZED*/return new Token(Token.APOSTROPHIZED,yytext(),-1);}
{hyphenated}                                                    {/*HYPHENATED*/return new Token(Token.HYPHENATED,yytext(),-1);}
"---"                                                           {/*PUNCTUATION*/return new Token(Token.PUNCTUATED,yytext(),-1);}
"--"                                                            {/*PUNCTUATION*/return new Token(Token.PUNCTUATED,yytext(),-1);}
[^ \n\t\ra-zA-Z0-9">""<"]                                       {/*PUNCTUATION*/return new Token(Token.PUNCTUATED,yytext(),-1);}
[^ \n\t\r]                                                      {/*ERRORS*/return new Token(Token.ERROR,yytext(),-1);}

