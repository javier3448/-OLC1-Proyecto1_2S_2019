/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compi1.proyecto1;

import FileManager.FileHelper;
import MyGUI.MainJFrame;
import MyGUI.TmpPruebas;
import MyObjects.MyError;
import ParserCSS.CssHelper;
import MyObjects.MyFileReader;
import ParserCSS.CssNode;
import ParserCSS.Synthesizer;
import UfeEnvironment.Runner;
import UfeEnvironment.CompListDel;
import UfeGraphviz.TreeDrawer;
import UfeInterfaces.AstNode;
import UfeNodes.Def;
import UfeNodes.Root;
import UfeNodes.Ufex.PropType;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author Alvarez
 */
public class Compi1Proyecto1 {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        String datPath = "ArchivosDePrueba/estilo1.css";
//        MyFileReader datReader;
//        try {
//            datReader = new MyFileReader(new File(datPath));
//        } catch (FileNotFoundException ex) {
//            System.out.println("error al leer el archivo dat");
//            Logger.getLogger(Compi1Proyecto1.class.getName()).log(Level.SEVERE, null, ex);
//            return;
//        }
//
//        ParserCSS.Lexer datLexer = new ParserCSS.Lexer(datReader);
//        while (true) {
//            try {
//                Symbol s = datLexer.next_token();
//                System.out.print(s.sym + ". " + ParserCSS.sym.terminalNames[s.sym]);
//                if (s.value != null) {
//                    System.out.print("  " + s.value.toString());
//                }
//                System.out.println();
//                if (s.sym == 0) {
//                    break;
//                }
//            } catch (IOException ex) {
//                System.out.println("escape!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                Logger.getLogger(Compi1Proyecto1.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

//        ParserCSS.parser datParser = new ParserCSS.parser(datLexer);
//        CssNode root = null;
//        try {
//            root = (CssNode) datParser.parse().value;
//        } catch (Exception ex) {
//            System.out.println("Error en el parseo");
//            Logger.getLogger(Compi1Proyecto1.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        Synthesizer.getInstance().generateGraphviz(root);
//        CssHelper helper;
//        Object obj = Synthesizer.getInstance().synthesizeCss(root);
//
//        if (obj instanceof CssHelper) {
//            helper = (CssHelper) obj;
//            System.out.println(helper.toString()); 
//        }
//        else{
//            System.out.println("Error");
//        }
        
        
//        System.out.println(Character.isJavaIdentifierStart('é'));
//        
//        String datPath = "C:\\Users\\Alvarez\\Documents\\1USAC\\src\\App.ufe";
//        MyFileReader datReader;
//        try {
//            datReader = new MyFileReader(new File(datPath));
//        } catch (FileNotFoundException ex) {
//            System.out.println("error al leer el archivo dat");
//            Logger.getLogger(Compi1Proyecto1.class.getName()).log(Level.SEVERE, null, ex);
//            return;
//        }
//        
//        ParserUFE.Lexer datLexer = new ParserUFE.Lexer(datReader);
//        int i = javax.swing.JTextField.LEFT;
//        while (true) {
//            try {
//                Symbol s = datLexer.next_token();
//                System.out.println(s.sym + ". " + ParserUFE.sym.terminalNames[s.sym]);
//                if (s.sym == 0) {
//                    break;
//                }
//                if (s.value != null) {
//                    System.out.println("-----" + s.value.toString());
//                }
//            } catch (IOException ex) {
//                System.out.println("escape!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                Logger.getLogger(Compi1Proyecto1.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        
//        ParserUFE.parser datParser = new ParserUFE.parser(datLexer);
//        Root root = null;
//        try {
//            root = (Root) datParser.parse().value;
//        } catch (Exception ex) {
//            System.out.println("Error en el parseo");
//            Logger.getLogger(Compi1Proyecto1.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        Def def = datParser.root.defs.get(0);
//        
//        TreeDrawer t = new TreeDrawer(root);
//        Runner r = new Runner(root);
//        CompListDel objLang = (CompListDel) r.visit(def);
//        
//        JFrame f = new JFrame();
//        f.setLayout(null);
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.setBounds(10,10, 500, 500);
//        
//        for(JComponent comp : objLang.components){
//            f.add(comp);
//        }
//        
//        f.setVisible(true);
//        
//        SimpleEntry<PropType, Object> se = new SimpleEntry(PropType.BORDER, "ja");
        
//           UfeRuntime.MyCompiler c = new UfeRuntime.MyCompiler();
//
////           JFrame jFrame = c.compile("ArchivosDePrueba\\USAC\\src\\App.ufe");
//           JFrame jFrame = c.compile("ArchivosDePrueba\\UfeProject\\src\\si.ufe");
//
//           jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//           jFrame.setVisible(true);

        createNecessaryDirectories();    
        MainJFrame mainFrame = new MainJFrame();
    }
    
     private static void createNecessaryDirectories() {
        File res = new File("res");
        boolean b0 =res.mkdir();
        File reportes = new File("reportes");
        boolean b1 =reportes.mkdir();
    }
    
    public static Object pr(){
        Character c = 'a';
        return c * c;
    }
}
