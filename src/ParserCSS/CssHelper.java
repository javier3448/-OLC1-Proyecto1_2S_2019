/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ParserCSS;

import MyObjects.MyError;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Alvarez
 */
public class CssHelper {    
    public class CssClass {
        //Integer son las constantes que se utilizan en la clase sym de css parser
        public HashMap<Integer, Object> properties;

        public CssClass(HashMap<Integer, Object> properties){
            this.properties = properties;
        }

        //SOLO PARA DEBUGGING
        @Override
        public String toString() {
            return properties.toString(); //To change body of generated methods, choose Tools | Templates.
        }
        
        
    }
    
    private HashMap<String, CssClass> cssClasses = new HashMap<String, CssClass>();
    private String path;
    
    public CssHelper(String path){
        this.path = path;
    }
    
    /**
     * ESTE METODO NO VERIFICA QUE LA CLASE PADRE DE KEY (si key se trata de una subclase) EXISTA EN CSSCLASSES. ESTA VERIFICACION DEBE SER REALIZADA EN EL AREA SINTACTICA
     * @param key debe ser una cadena sin espacios o una cada sin especios seguida de un espacio y otro cadena sin espacio: [^ ]+[ ][^ ] la expresion regular seria algo asi
     * @param cssClass
     * @return 
     */
    public MyError add(String key, HashMap<Integer, Object> properties){
        key = key.toLowerCase(Locale.ROOT);
        CssClass cssClass = new CssClass(properties);
        if (cssClasses.putIfAbsent(key, cssClass) != null) {
            return new MyError("CssHelper.add: Ya esxite una clase con el nombre: <" + key + ">");//El path lo pasaria el sintetizador ufe
        }
        return null;
    }
    
     /**
     * ESTE METODO NO VERIFICA QUE LA CLASE PADRE DE KEY (si key se trata de una subclase) EXISTA EN CSSCLASSES. ESTA VERIFICACION DEBE SER REALIZADA EN EL AREA SINTACTICA
     * ESTE METODO NO VERIFICA QUE KEY SEA UN STRING SEA UNA LLAVE VALIDA (i.e.) id id | id
     * @param key formato "idClase idSubclase" o "idClase"
     * @return tipo MyError si ocurrio un error al buscar key en cssClasses, de lo contrario, retorna un UfeComponentStyle con propiedades de style html separadas por ;
     */
    public Object get(String key){
        key = key.toLowerCase(Locale.ROOT);
        CssClass child = cssClasses.get(key);
        if (child == null) {
            return new MyError("La clase con nombre: <" + key + ">. No existe en el cssHelper con ruta: <" + path + ">");
        }
        int indexOfSpace = key.indexOf(' ');
        //Revisa si key es una llave que representa una clase no hijo
        if(indexOfSpace == -1){
            return new UfeComponentStylizer(child.properties);
        }
        else{
            String parentKey = key.substring(0, indexOfSpace);
            CssClass parent = cssClasses.get(parentKey);
            HashMap<Integer, Object> combinedProperties = new HashMap<>(parent.properties);
            
            for(Map.Entry<Integer, Object> property : child.properties.entrySet()) {
                Integer propertyKey = property.getKey();
                Object value = property.getValue();

                combinedProperties.put(propertyKey, value);
            }
            return new UfeComponentStylizer(combinedProperties);
        }
    }

    public String getPath() {
        return path;
    }
    
    //SOLO PARA DEBUGGIGN
    @Override
    public String toString() {
        return  cssClasses.toString().replace("},", "}\n");
    }
}
