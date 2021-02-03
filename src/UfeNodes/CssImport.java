/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeNodes;

import UfeInterfaces.AstNode;
import UfeInterfaces.UfeVisitor;

/**
 *
 * @author Alvarez
 */
public class CssImport extends AstNode{
    public String path;

    public CssImport(String path) {
        this.path = path;
    }

    @Override
    public Object accept(UfeVisitor v) {
        return v.visit(this);
    }
    
    
    
}
