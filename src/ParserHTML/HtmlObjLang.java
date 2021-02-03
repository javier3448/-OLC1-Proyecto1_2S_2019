/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ParserHTML;

import java.util.LinkedList;

/**
 *
 * @author Alvarez
 */
public class HtmlObjLang {
    public String noufe;
    public String title;
    public LinkedList<String> idDivs;

    
    
    public HtmlObjLang(String noufe, String title, LinkedList<String> idDivs) {
        this.noufe = (noufe == null ? defaults.NOUFE : noufe);
        this.title = (title == null ? defaults.TITLE : title);
        this.idDivs = idDivs;
    }
    
    private static final class defaults{
        private static final String NOUFE = "NO UFE DEFAULT";
        private static final String TITLE = "Titulo default";
    }
}
