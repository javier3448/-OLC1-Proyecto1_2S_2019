/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeEnvironment;

import MyObjects.MyError;
import MyObjects.MyFileReader;
import UfeNodes.Root;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;

/**
 * delegate de otro runner. 
 * @author Alvarez
 */
public class ImportedInstance {
    
    public final String path; // el path origen (no la carpeta, el archivo .ufe como tal)
    public LinkedList<String> ids = new LinkedList<String>();//los id component que han sido importados
    
    private Runner ufeRunner;//delegated

    private ImportedInstance(String path, Runner ufeRunner/*runner ya incializado con las variables globales*/){
        this.path = path;
        this.ufeRunner = ufeRunner;
    }
    
    public static ImportedInstance create(String path){
        MyFileReader datReader;
        try {
            datReader = new MyFileReader(new File(path));
        } catch (FileNotFoundException ex) {
            MyError error = new MyError (MyError.MyErrorType.UNKNOWN, MyError.Phase.PREPROCESS, path, -1, -1, "Error al leer el .ufe. Direccion no valida: <" + path + ">");
            MySystem.Console.println(error);
            return null;
        }
        
        ParserUFE.Lexer datLexer = new ParserUFE.Lexer(datReader);
        
        ParserUFE.parser datParser = new ParserUFE.parser(datLexer);
        Root root = null;
        try {
            root = (Root) datParser.parse().value;
        } catch (Exception ex) {
            return null; //el error ya fue reportado por el parser
        }
        if (root == null) {
            System.out.println("    ImportedInstance.create: root null");
            return null; //error ya reportado por el parser
        }
        
        return new ImportedInstance(path, new Runner(root));
    }
    
    /**
     * 
     * @param id
     * @return null si no fue importado un componente con ese nombre, de lo contrario retorna el CompListDel o el error de .visitDef
     */
    public Object visitDef(String id) {
        if (ids.contains(id)) {
            return ufeRunner.getComponentById(id);
        }
        return null;
    }

    /**
     * 
     * @param id
     * @return null si se agrega, MyError solo con mesaje si no se logro agregar
     */
    public MyError addId(String id) {
        if (!ufeRunner.containsCompDef(id)) {
            return new MyError("No exite una definicion para el componente: " + id + " en: " + path);
        }
        for (String definedId : ids) {
            if (definedId.equals(id)) {
                return new MyError("Ya existe la importacion para el componente: "+ id + " en: " + path);
            }
        }
        ids.add(id);
        return null;
    }
    
    
    
}
