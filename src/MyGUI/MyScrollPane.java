/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyGUI;

import java.awt.Font;
import java.io.File;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * Chapus menor, no es necesaria la variable nombre, la variable carpeta deberia de traer incluido un File con el nombre de el archivo, en vez de traer solo el directorio
 * @author Alvarez
 */
public class MyScrollPane extends JScrollPane{
    public File file;
    //Debe de incluir la extencion del archivo tambien
    public String nombre;
    public JTextPane textPane ;

    //N
    MyScrollPane(JTextPane textPane, File file, String nombre) {
        super(textPane);
        this.textPane = textPane;
        this.textPane.setFont(new Font("Courier New", Font.PLAIN, 13));
        this.file =file;
        this.nombre = nombre;
    }
    
    MyScrollPane(){
        super();
    }
}
