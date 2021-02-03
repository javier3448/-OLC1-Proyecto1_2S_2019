/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeEnvironment;

import java.util.LinkedList;
import javax.swing.JComponent;

/**
 * JComponent List delegate
 * Resultado de visitar un nodo def
 * @author Alvarez
 */
public class CompListDel {
    public LinkedList<JComponent> components;

    public CompListDel(LinkedList<JComponent> components) {
        this.components = components;
    }
}
