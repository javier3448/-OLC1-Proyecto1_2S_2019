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
public class UfeImport extends AstNode{
    public String id;
    public String path;

    public UfeImport(String id, String path) {
        this.id = id;
        this.path = path;
    }
    
    @Override
    public Object accept(UfeVisitor v) {
        return v.visit(this);
    }
    
    
}
