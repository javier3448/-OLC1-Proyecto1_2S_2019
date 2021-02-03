/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeInterfaces;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;

/**
 *
 * @author Alvarez
 */
public abstract class AstNode implements Visitable{
    protected int line;
    protected int column;
    
    //SOLO PARA DEBUGGING. NO SIRVE SI SE SOBRE ESCRIBE toString();
    public final String dotId(){
        return this.toString().replace(".", "").replace("@", "").replace("$", "");
    }
    
    public final int getLine() {
        return line;
    }

    public final void setLine(int line) {
        this.line = line;
    }

    public final int getColumn() {
        return column;
    }

    public final void setColumn(int column) {
        this.column = column;
    }
    
    /**
     * CHAPUZ MEDIO PARA PODER AGREGAR LA LINE A Y COLUMNA DE UN ERROR SIN CAMBIAR MUCHO EN CUP
     * Setea la linea y columna donde empieza el error. No tiene relacion con el setLocation de Cup.ComplexSymbol
     * @param line
     * @param column 
     */
    public final void setLocation(int line, int column){
        this.line = line;
        this.column = column;
    }
}
