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
 *
 * @author Alvarez
 */
public class Root extends AstNode{
    public String path;
    /**
     * declaracion y asignacion de variables locales
     */
    public LinkedList<Stmt> stmts;
    public LinkedList<UfeImport> ufeImports;
    public CssImport cssImport;
    public LinkedList<Render> renders;
    public LinkedList<Def> defs;

    public Root(String path, LinkedList<Stmt> stmts, LinkedList<UfeImport> ufeImports, CssImport cssImport, LinkedList<Render> renders, LinkedList<Def> defs) {
        this.path = path;
        this.stmts = stmts;
        this.ufeImports = ufeImports;
        this.cssImport = cssImport;
        this.renders = renders;
        this.defs = defs;
    }

    @Override
    public Object accept(UfeVisitor v) {
        return v.visit(this);
    }
    
    
}
