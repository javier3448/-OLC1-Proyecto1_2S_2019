/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeEnvironment;

import MyObjects.MyError;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Optional;

/**
 *
 * @author Alvarez
 */
public class Scope {

    private static void si() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public final HashMap<String, Optional<Object>> symbolTable = new HashMap<>();//Chapuz maximo para conseguir las variables para hacer la tabla de simbolos en el MainFrame
    
    private final Scope outerScope;
    
    public Scope(Scope outerScope){//outerScope puede ser null
        this.outerScope = outerScope;
    }
    
    public Scope(){
        this.outerScope = null;
    }

    public Scope getOuterScope() {
        return outerScope;
    } 
    
    /**
     * Retorna verdadero si su outerScope es null
     * @return 
     */
    public boolean isGlobal(){
        return outerScope == null;
    }
    
    public MyError add(String key, Object value){
        key = key.toLowerCase(Locale.ROOT);
        if (value instanceof MyError) {
            return new MyError("La variable: " + key + ". No puede el almacenar el tipo: " + value.getClass().getName());
        }
        Optional<Object> opt;
        //Verifica que value si es null
        if(value == null){
            opt = Optional.empty();
        }
        else{
            opt = Optional.of(value);
        }
        
        if (symbolTable.putIfAbsent(key, opt) != null) {
            return new MyError("Ya existe un symbolo con key: " + key + ". En la tabla de simbolos");
        }
        return null;
    }
    
    //tipo MyError si no existe, null si existe la llave pero esta vacia, y obj
    public Object get(String key){
        key = key.toLowerCase(Locale.ROOT);
        Optional<Object> opt = symbolTable.get(key);
        if (opt == null) {
            if (outerScope == null) {
                return new MyError("SymbolTable.get: No existe el simbolo: " + key + ", en la tabla de simbolos");
            }
            return outerScope.get(key);
        }
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }
    
    //value es el nuevo valor, throws null pointer if value is null
    public MyError set(String key, Object value){
        key = key.toLowerCase(Locale.ROOT);
        if (value instanceof MyError) {
            return new MyError("La variable: " + key + ". No puede el almacenar el tipo: " + value.getClass().getName());
        }
        
        Optional<Object> opt;
        if (value == null) {
            opt = Optional.empty();
        }
        else{
            opt = Optional.of(value);
        }
        //obj retorna null si no existe la key en la tabla, el valor sin cambiar si existe pero no tiene el mismo tipo que value
        Optional<Object> result = symbolTable.computeIfPresent(key, (k, val) -> opt);
        if (result == null) {
            if (outerScope != null) {
                return outerScope.set(key, value);
            }
            return new MyError("SynmbolTable.set: No existe el simbolo: " + key + ", en la tabla de simbolos");
        }
        return null;
    }
    
    public static void main(String[] args){
        Scope st = new Scope();
        MyError error = st.add("nombre", null);
        if (error != null) {
            System.out.println(error.msg);
        }
        System.out.println(st.get("nombre"));
        error = st.set("nombre", "javiernuevo");
        error = st.set("nombre", null);
        error = st.set("nombre", new Integer(30));
        String[] strings = {"Id", "Nombre", "Double", "Jaja"};
        System.out.println(st.get("nombre"));
        if (error != null) {
            System.out.println(error.msg);
        }
        
        String n = null;
        si();
//        n.toString();
    }
    
}
