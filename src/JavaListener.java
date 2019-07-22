import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaListener extends Java8BaseListener {
    Pattern camelCase = Pattern.compile("^[A-Z][a-zA-Z0-9]*");
    Pattern hasNumber = Pattern.compile("[0-9]+");
    public void sugerencirNombreClaseInterface(String id,int line, int column){
        Matcher camelMatcher = camelCase.matcher(id);
        Matcher numberMatcher = hasNumber.matcher(id);
        if (!camelMatcher.matches()||numberMatcher.matches()){
            Character ch = id.charAt(0);
            String newNameClass = Character.toUpperCase(ch)+ id.substring(1);
            newNameClass = newNameClass.replaceAll("[0-9]","");
            System.out.printf("<%d,%d> Se sugiere cambiar el nombre de la clase %s por %s \n",line,column,id,newNameClass);
        }

    }

    /**
     * verificar que el nombre de la clase siga una
     * nomenclatura CamelCase y no tenga numeros en su nombre
     */

    @Override
    public void enterClassDeclaration(Java8Parser.ClassDeclarationContext ctx) {
        int column = (ctx.normalClassDeclaration().Identifier().getSymbol().getCharPositionInLine()+1);
        int line = (ctx.normalClassDeclaration().Identifier().getSymbol().getLine());
        String id = ctx.normalClassDeclaration().Identifier().toString();
        sugerencirNombreClaseInterface(id,line,column);


    }

    /**
     *   Convenciones para los nombres
     */

    @Override
    public void enterInterfaceDeclaration(Java8Parser.InterfaceDeclarationContext ctx) {
        Token tk = ctx.normalInterfaceDeclaration().Identifier().getSymbol();
        if (tk != null){
            int line = tk.getLine();
            int column = tk.getCharPositionInLine();
            sugerencirNombreClaseInterface(tk.getText(),line,column);
            return;

        }
        tk = ctx.annotationTypeDeclaration().Identifier().getSymbol();
        if (tk!=null){
            int line = tk.getLine();
            int column = tk.getCharPositionInLine();
            sugerencirNombreClaseInterface(tk.getText(),line,column);
        }
    }

    /**
     * Una sola declaracion por linea
     */
    @Override
    public void enterVariableDeclaratorList(Java8Parser.VariableDeclaratorListContext ctx) {
        if (ctx.variableDeclarator().size()>1){
            System.out.printf("<Linea:%d> Se sugiere hacer una declaracion por linea \n",ctx.variableDeclarator().get(0).variableDeclaratorId().Identifier().getSymbol().getLine());
        }
    }

  /*@Override
  public void enterConstantDeclaration(Java8Parser.ConstantDeclarationContext ctx) {

    if (ctx.variableDeclaratorList().variableDeclarator().size()>1){
      System.out.printf("<Linea:%d> Se sugiere hacer una declaracion por linea \n",ctx.variableDeclaratorList().variableDeclarator().get(0).variableDeclaratorId().Identifier().getSymbol().getLine());
    }
    ctx.variableDeclaratorList().variableDeclarator().forEach(vd ->{
      Token tk = vd.variableDeclaratorId().Identifier().getSymbol();
      if (!tk.equals(tk.toString().toUpperCase())){
        System.out.printf("<%d> Se sugiere hacer esta linea mas corta dado que mide %d\n",tk.getLine(),tk.getCharPositionInLine());
      }

    });
  }*/


    /**
     * Verifica una sola declaracion por linea
     * @param ctx
     */
    @Override
    public void enterPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
        Token tk = ctx.getStop();

        if ( tk.getCharPositionInLine() > 100){
            System.out.printf("<%d> Se sugiere hacer esta linea mas corta dado que mide %d\n",tk.getLine(),tk.getCharPositionInLine());
        }

    }

    /**
     * Verifica el nombre del paquete
     * @param ctx
     */
    @Override
    public void enterPackageName(Java8Parser.PackageNameContext ctx) {
        Token tk = ctx.Identifier().getSymbol();
        if (!tk.getText().equals(tk.getText().toLowerCase())){
            System.out.printf("<%d,%d> Se sugiere cambiar el nombre del paquete por %s\n",tk.getLine(),tk.getCharPositionInLine(),tk.getText().toLowerCase());
        }
    }

    /**
     * verifica la longitud de linea en una declaracion
     *
     * @param ctx
     */
    @Override
    public void enterImportDeclaration(Java8Parser.ImportDeclarationContext ctx) {
        Token tk = ctx.getStop();
        if ( tk.getCharPositionInLine() > 100){
            System.out.printf("<%d> Se sugiere hacer esta linea mas corta dado que mide %d\n",tk.getLine(),tk.getCharPositionInLine());
        }
    }

    /**
     * Una declaracion por linea
     * @param ctx
     */
    @Override
    public void enterLocalVariableDeclarationStatement(Java8Parser.LocalVariableDeclarationStatementContext ctx) {
        Token tk = ctx.getStop();
        if ( tk.getCharPositionInLine() > 100){
            System.out.printf("<%d> Se sugiere hacer esta linea mas corta dado que mide %d\n",tk.getLine(),tk.getCharPositionInLine());
        }

    }


    /**
     * identacion del bloque y una declaracion por linea
     * @param ctx
     */
    @Override
    public void enterBlock(Java8Parser.BlockContext ctx) {
        int initIdent = ctx.getStart().getCharPositionInLine();
        int initLine = ctx.getStart().getLine();

        if (ctx.blockStatements() != null){
            for (ParserRuleContext newBlock: ctx.blockStatements().blockStatement()){
                Token tk = newBlock.getStart();
                int newLine = tk.getLine();
                if (newLine == initLine){
                    System.out.printf("<%d,%d> se recomienda una declaracion por linea\n",tk.getLine(), tk.getCharPositionInLine());

                }
                else if (tk.getCharPositionInLine() - initIdent != 2){
                    System.out.printf("<%d,%d> se recomienda dos espacios para la identacion del bloque\n",tk.getLine(), tk.getCharPositionInLine());
                }
                initLine = newLine;
            }
            int endIdent = ctx.getStop().getCharPositionInLine();
            if(initIdent != endIdent){
                System.out.printf("<%d,%d> se recomienda que el nivel de identacion en la apertura y cierre de bloques sea igual \n",ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
            }
        }

    }
}
