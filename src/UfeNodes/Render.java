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
public class Render extends AstNode{
    public String compId;
    public String divId;

    public Render(String compId, String divId) {
        this.compId = compId;
        this.divId = divId;
    }
    
    @Override
    public Object accept(UfeVisitor v) {
        return v.visit(this);
    }
    
}
