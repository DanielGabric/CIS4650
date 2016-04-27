JAVA=java
JAVAC=javac
JFLEX=jflex
CLASSPATH=-classpath /usr/share/java/cup.jar:.
#CLASSPATH=-classpath .:/Users/danielgabric/Downloads/java-cup-11a.jar
CUP=cup

all: Main.class

Main.class: ast/*.java parser.java sym.java Lexer.java Main.java type/*.java Semantic.java

%.class: %.java
	$(JAVAC) $(CLASSPATH)  $^

Lexer.java: lex.flex
	$(JFLEX) lex.flex

parser.java: syntactic.cup
	#java -jar /Users/danielgabric/Downloads/java-cup-11a.jar -dump -expect 3 syntactic.cup
	$(CUP) -dump -expect 3 syntactic.cup

clean:
	rm -f parser.java Lexer.java sym.java *.class ast/*.class type/*.class *~
