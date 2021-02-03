/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyObjects;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 *
 * @author Alvarez
 */
public class MyFileReader extends FileReader{
    
    private String path;
    
    public MyFileReader(File file) throws FileNotFoundException {
        super(file);
        this.path = file.getPath();
    }
    
    //Chapuz medio para que se pueda conseguir la direccion del archivo desde el scanner y el parser
    public String getPath(){
        return path;
    }
    
}
