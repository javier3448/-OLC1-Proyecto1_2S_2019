/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeInterfaces;


/**
 *
 * @author Alvarez
 */
public interface Visitable {
    public Object accept(UfeVisitor v);
}
