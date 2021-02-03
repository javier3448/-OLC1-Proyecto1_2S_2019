/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileManager;

/**
 *
 * @author Alvarez
 */
public class GeneradorCup {
    public static void main(String[] args){
        generarCss("src/ParserCSS/parserSpecification.cup");
        generarHtml("src/ParserHTML/parserSpecification.cup");
        generarUfe("src/ParserUFE/parserSpecification.cup");
    }
    public static void generarCss(String path){
        String opciones[] = new String[4];
        
        opciones[0] = "-destdir";
        
        opciones[1] = "src/ParserCSS";
        
        opciones[2] = "-nonterms";
        
        opciones[3] = path;
        
        try
        {
            java_cup.Main.main(opciones);
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            System.out.println("error en el generador sintactico CSS");
        }
    }
    
    public static void generarHtml(String path){
        String opciones[] = new String[3];
        
        opciones[0] = "-destdir";
        
        opciones[1] = "src/ParserHTML";
        
        opciones[2] = path;
        
        try
        {
            java_cup.Main.main(opciones);
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            System.out.println("error en el generador sintactico HTML");
        }
    }    
    
    public static void generarUfe(String path){
        String opciones[] = new String[4];
        
        opciones[0] = "-destdir";
        
        opciones[1] = "src/ParserUFE";
        
        opciones[2] = "-nonterms";
        
        opciones[3] = path;
        
        try
        {
            java_cup.Main.main(opciones);
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            System.out.println("error en el generador sintactico UFE");
        }
    }  
}
