/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeNodes;

import UfeInterfaces.AstNode;
import UfeInterfaces.UfeVisitor;
import java.util.LinkedList;

/**
 * CHAPUZ ALTO deveria de tener un block en vez de una lista de stmts pero seria mucho tabajo cambiar la gramatica y las clases nodo
 * Entonces se definio que Def hace un push/pop scope al ejecutarse
 * @author Alvarez
 */
public class Def extends AstNode{

    public String componentId;
    public LinkedList<Stmt> stmts;

    public Def(String componentId, LinkedList<Stmt> stmts) {
        this.componentId = componentId;
        this.stmts = stmts;
    }

    @Override
    public Object accept(UfeVisitor v) {
        return v.visit(this);
    }
    
}
