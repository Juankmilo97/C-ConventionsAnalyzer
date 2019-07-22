import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
// import java.io.File;

public class Analyzer {
    public static void main(String[] args) throws Exception {

    int selector = 0; //selector = 0 --> Java || selector = 1 --> C++

    if(selector == 0) {

        try {
            Java8Lexer lexer;
            if (args.length > 0) {
                lexer = new Java8Lexer(CharStreams.fromFileName(args[0]));
            } else {
                lexer = new Java8Lexer(CharStreams.fromStream(System.in));
            }

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            Java8Parser parser = new Java8Parser(tokens);

            ParseTree tree = parser.compilationUnit();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(new JavaListener(), tree);


        } catch (Exception e) {
            System.err.println(e.getMessage());

        }
    }else if(selector == 1){
        try {
            // crear un analizador léxico que se alimenta a partir de la entrada (archivo  o consola)
            CPP14Lexer lexer;
            if (args.length > 0)
                lexer = new CPP14Lexer(CharStreams.fromFileName(args[0]));
            else
                lexer = new CPP14Lexer(CharStreams.fromStream(System.in));
            // Identificar al analizador léxico como fuente de tokens para el sintactico
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            // Crear el objeto correspondiente al analizador sintáctico que se alimenta a partir del buffer de tokens
            CPP14Parser parser = new CPP14Parser(tokens);
            ParseTree tree = parser.translationunit(); // Iniciar el análisis sintáctico en la regla inicial:
            //System.out.println(tree.toStringTree(parser)); // imprime el arbol al estilo LISP
            // Create a generic parse tree walker that can trigger callbacks

            ParseTreeWalker walker = new ParseTreeWalker();
            // Walk the tree created during the parse, trigger callbacks

            walker.walk(new CPPListener(), tree);
            System.out.println(); // print a \n after translation

        } catch (Exception e) {
            System.err.println("Error (Test): " + e);
        }
    }
  }
}
