/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeRuntime;

import MyObjects.MyError;
import MyObjects.MyFileReader;
import ParserHTML.HtmlObjLang;
import UfeEnvironment.CompListDel;
import UfeEnvironment.ImportedInstance;
import UfeEnvironment.Runner;
import UfeNodes.Def;
import UfeNodes.Render;
import UfeNodes.Root;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Wrapper de runner, ejecuta las funciones de un linker
 * @author Alvarez
 */
public class MyCompiler {
    
    private Runner ufeRunner;
    
    private LinkedList<Render> renders;
    private HtmlObjLang htmlObjLang;
    
    public MyCompiler(){
    }

    public JFrame compile(String path){
        
        if (!initHtmlObjLang(path)) {
            return null;
        }
        
        if (!initUfeObjLang(path)) {
            return null;
        }
        
        JFrame jFrame = new JFrame();
        jFrame.setLayout(null);
        jFrame.setTitle(htmlObjLang.title);
        
        int frameWidth = 0;
        int frameHeight = 0;
        
        for(Render render : renders){
            String compId = render.compId.toLowerCase(Locale.ROOT);
            String divId = render.divId.toLowerCase(Locale.ROOT);
            if (!(htmlObjLang.idDivs.contains(divId))) {
                JLabel lbl = new JLabel("<html>" + htmlObjLang.noufe + "</html>");
                lbl.setBounds(0, 0, 100, 100);
                jFrame.add(lbl);
                frameWidth = 100 > frameWidth ? 100 : frameWidth;
                frameHeight = 100 > frameHeight ? 100 : frameHeight;
                continue;
            }
            Object obj = ufeRunner.getComponentById(compId);
            if (obj == null) {
                String msg = "El componente: " + compId + " es null.";
                MyError error = new MyError(MyError.MyErrorType.SEMANTIC, MyError.Phase.LINK, path, render.getLine(), render.getColumn(), msg);
                MySystem.Console.println(error);
                continue;
            }
            if (!(obj instanceof CompListDel)) {
                String msg = "No se puede agregar un: [" + obj.getClass().getSimpleName() + "] al div: " + divId;
                MyError error = new MyError(MyError.MyErrorType.SEMANTIC, MyError.Phase.LINK, path, render.getLine(), render.getColumn(), msg);
                MySystem.Console.println(error);
                continue;
            }
            CompListDel comps = (CompListDel) obj;
            for(JComponent comp : comps.components){
                int x = comp.getX() + comp.getWidth();
                int y = comp.getY() + comp.getHeight();
                frameWidth = x > frameWidth ? x : frameWidth;
                frameHeight = y > frameHeight ? y : frameHeight;
                
                jFrame.add(comp);
            }
        }
        
        jFrame.setBounds(0, 0, frameWidth, frameHeight);
        
        return jFrame;
    }

    /**
     * 
     * @param path
     * @return false si no se logro inicializar con exito
     */
    private boolean initUfeObjLang(String path) {
        
        MyFileReader datReader;
        try {
            datReader = new MyFileReader(new File(path));
        } catch (FileNotFoundException ex) {
            MyError error = new MyError (MyError.MyErrorType.UNKNOWN, MyError.Phase.PREPROCESS, path, -1, -1, "Error al leer el .ufe. Direccion no valida: <" + path + ">");
            MySystem.Console.println(error);
            return false;
        }
        
        ParserUFE.Lexer datLexer = new ParserUFE.Lexer(datReader);
        
        ParserUFE.parser datParser = new ParserUFE.parser(datLexer);
        Root root = null;
        try {
            root = (Root) datParser.parse().value;
        } catch (Exception ex) {
            return false; //el error ya fue reportado por el parser
        }
        if (root == null) {
            System.out.println("root null");
            return false; //error ya reportado por el parser
        }
        
        renders = root.renders;
        
        ufeRunner = new Runner(root);
        
        return true;
    }

    /**
     * 
     * @param path
     * @return 
     */
    private boolean initHtmlObjLang(String path) {
        
        //Conseguir la direccion de index.html
        path = (new File(path)).getParentFile().getParent() + File.separator + "public" + File.separator + "index.html";
        
        MyFileReader datReader;
        try {
            datReader = new MyFileReader(new File(path));
        } catch (FileNotFoundException ex) {
            MyError error = new MyError (MyError.MyErrorType.UNKNOWN, MyError.Phase.PREPROCESS, path, -1, -1, "Error al leer el .index. Direccion no valida: <" + path + ">");
            MySystem.Console.println(error);
            return false;
        }
        
        ParserHTML.Lexer datLexer = new ParserHTML.Lexer(datReader);
        ParserHTML.parser datParser = new ParserHTML.parser(datLexer);
        HtmlObjLang root = null;
        try {
            root = (HtmlObjLang) datParser.parse().value;
        } catch (Exception ex) {
            return false;
        }

        htmlObjLang = root;
        
        return true;
    }
    
    /**
     * Chapuz alto. No se asegura que se haya hecho la compilacion antes. Seria mejor que .compile retornara un obj que tenga el frame y los getColumnName o que esta clase solo se pueda inicializar como el Imported instance
     * @return 
     */
    public Object[] getColumnNames(){
        if (ufeRunner == null || renders == null || htmlObjLang == null) {
            return null;
        }
        String[] columnNames = {"Name", "Value"};
        return columnNames;
    }
    
    /**
     * Chapuz alto. No se asegura que se haya hecho la compilacion antes. Seria mejor que .compile retornara un obj que tenga el frame y los getTableData o que esta clase solo se pueda inicializar como el Imported instance
     * @return 
     */
    public Object[][] getTableData(){
        if (ufeRunner == null || renders == null || htmlObjLang == null) {
            return null;
        }
        final ArrayList<Object[]> lista = new ArrayList<>();
        Object[] fila = new Object[2];
        fila[0] = "CssHelper";
        if (ufeRunner.cssHelper == null) {
            fila[1] = "null";
        }else{
            fila[1] = ufeRunner.cssHelper.getPath();
        }
        lista.add(fila);
        fila = new Object[2];
        fila[0] = "";
        fila[1] = "";
        lista.add(fila);
        for (ImportedInstance ufeImport : ufeRunner.imports) {
            fila = new Object[2];
            fila[0] = "UfeImport";
            fila[1] = ufeImport.path;
            lista.add(fila);
        }
        if (ufeRunner.imports.size() < 1) {
            fila = new Object[2];
            fila[0] = "Sin ufe imports";
            fila[1] = "Sin ufe imports";
            lista.add(fila);
        }
        fila = new Object[2];
        fila[0] = "";
        fila[1] = "";
        lista.add(fila);
        ufeRunner.scope.symbolTable.forEach((key, val)->{
            Object[] f = new Object[2];
            f[0] = key;
            if (val.isPresent()) {
                Object obj = val.get();
                if (obj instanceof Object[]) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("{ ");
                    for (Object o : (Object[])obj) {
                        sb.append(o.toString()).append(",");
                    }
                    sb.setLength(sb.length() - 1);
                    sb.append(" }");
                    f[1] = sb.toString();
                }else{
                    f[1] = obj.toString();
                }
            }else{
                f[1] = "null";
            }
            lista.add(f);
        });
        Object[][] result = new Object[lista.size()][2];
        for (int i = 0; i < lista.size(); i++) {
            fila = lista.get(i);
            for (int j = 0; j < 2; j++) {
                result[i][j] = fila[j];
            }
        }
        return result;
    }
}
