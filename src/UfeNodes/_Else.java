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
public class _Else extends AstNode{
    public Stmt stmt; 

        public _Else(Stmt content) {
            this.stmt = content;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
}
