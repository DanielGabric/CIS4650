all: Scanner

Scanner: jflex Scanner.java Lexer.java Token.java
	javac Lexer.java Scanner.java Token.java

jflex: lex.flex
	jflex lex.flex

run: Scanner.class
	java Scanner

clean:
	rm *.class
	rm Lexer.*