/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyObjects;

/**
 *
 * @author Alvarez
 */
public abstract class AstNodeInfo {
    protected int line;
    protected int column;
    protected Object value;

    public AstNodeInfo(int line, int column, Object value) {
        this.line = line;
        this.column = column;
        this.value = value;
    }
    
    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    
}
