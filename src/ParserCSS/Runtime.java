/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ParserCSS;

import MyObjects.MyError;
import MyObjects.MyError.MyErrorType;
import MyObjects.MyError.Phase;
import MyObjects.MyFileReader;
import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author Alvarez
 */
public class Runtime {
    
    public static Runtime instance = new Runtime();

    private Runtime() {
    }
    
    public static Runtime getInstance(){
        return instance;
    }
    
    public CssHelper compile(String path){
        MyFileReader datReader;
        try {
            datReader = new MyFileReader(new File(path));
        } catch (FileNotFoundException ex) {
            MyError error = new MyError(MyError.MyErrorType.UNKNOWN, Phase.CSS, path, -1, -1, ex.getMessage());
            MySystem.Console.println(error);
            return null;
        }

        ParserCSS.Lexer datLexer = new ParserCSS.Lexer(datReader);
        ParserCSS.parser datParser = new ParserCSS.parser(datLexer);
        CssNode root = null;
        try {
            root = (CssNode) datParser.parse().value;
        } catch (Exception ex) {
            MyError error = new MyError(MyError.MyErrorType.SYNTAX, Phase.CSS, path, -1, -1, ex.getMessage());
            MySystem.Console.println(error);
            return null;
        }
        
        if (root == null) {
            MyError error = new MyError(MyError.MyErrorType.SYNTAX, Phase.CSS, path, -1, -1, "Error sintactico al leer el archivo: " + path + " no se genero el css. ");
            MySystem.Console.println(error);
            return null;
        }

        root.setValue(path);//CHAPUZ MAXIMO. NO SE PORQUE PERO EL PARSER NO LE SETEO LE PATH AL .value de su root resultado
        
        CssHelper helper;
        Object obj = Synthesizer.getInstance().synthesizeCss(root);
        
        if (obj instanceof CssHelper) {
            helper = (CssHelper) obj;
        }
        else{
            MySystem.Console.println((MyError)obj);
            return null;
        }
        return helper;
    }
}
