/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileManager;

import java.io.File;

/**
 *
 * @author Alvarez
 */
public class GeneradorJFlex {
    public static void main(String[] args)
    {
//        //GENERADOR DE CSS
        String pathCss = "src/ParserCSS/scannerSpecification.jflex";
        generarLexer(pathCss);
        //GENERADOR DE HTML
        String pathHtml = "src/ParserHTML/scannerSpecification.jflex";
        generarLexer(pathHtml);
//        //GENERADOR DE UFE
        String pathUfe = "src/ParserUFE/scannerSpecification.jflex";
        generarLexer(pathUfe);
    }
    
    public static void generarLexer(String path)
    {
        File file = new File(path);
        jflex.Main.generate(file);
    }
}
