Warmup Assignment
Name: Daniel Gabric

Description: Lex/Scan an SGML file and check that the structure and syntax is correct.


Limitations:  Since the document didn't specify what is allowed to be in an OPEN-TAG/CLOSE-TAG, I 
              guessed that the identifier is only allowed to have alphanumeric characters with _ and -, 
              and that attributes can be any non white space character and non <> character.
              Since it was not specified what exactly is and isn't punctuation, I assumed that < and >
              cannot be punctuation and that if they're not a part of an open/close tag that it is
              an error.


Build/Run:    To build this project, one only needs to open the terminal, navigate to this directory, then
              type and enter 'make'. To run, one needs to type in 'make run', and if you want to redirect stdin
              to a file type in 'make run < FILENAME'.

Improvements: If I was given more time and more specifics on the project. I would make the scanner actually recognize
              proper attributes in OPEN tags. I would also implement the syntax checking with Yacc because it would
              allow me more flexibility with my regular expressions and checking that tags are correct.


Test Plan:    I made the following test case to test every faucet of the program, so to test the regular expressions, and
              errors when it comes to nesting and filtering out irrelevant tags and info. The test case is as follows

<P> -048329.432 </P>
<DocID> hello </DocID>
<Doc>
<P>hyp-hen-ated</P>
<headline>a-post-r'ophi'zed </headline>
<TEXT>
-0494.423
+04
59
.432
3123.
NUMBERS EVERYWHERE
</TEXT>
;sdaf;as'dgad;fg'dfg'asdfg;
---
--
-
justarandomword
<ERRORNEST>
<P>
</ERRORNEST>
</P>
<OPENTAGWITHOUTCLOSE>
</CLOSETAGWITHOUTOPEN>
<

</Doc>