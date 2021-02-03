/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ParserCSS;

import MyObjects.AstNodeInfo;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Alvarez
 */
public class CssNode extends AstNodeInfo{
    private LinkedList<CssNode> children = new LinkedList<CssNode>();
    private NodeTag tag;

    public CssNode(NodeTag tag,int line, int column, Object value) {
        super(line, column, value);
        this.tag = tag;
    }
    
    public void addChild(CssNode child){
        if (child == null) {
            throw new IllegalArgumentException("addChild: parameter <child> cant be null");
        }
        children.add(child);
    }
    
    public CssNode getChild(int index){
        return children.get(index);
    }

    public LinkedList<CssNode> getChildren() {
        return children;
    }

    public NodeTag getTag() {
        return tag;
    }
    
    public static void main(String args[]){
        SimpleEntry<String, HashMap<String, Object>> entry = new SimpleEntry<>("id", new HashMap<>());
    }
    
    
    //los nodos: l_class y l_subclass no son necesarios. Si se abstrae mas el arbolo ast se pudieron haber omitido
    public enum NodeTag{
        CLASS, L_CLASS, SUBCLASS, L_SUBCLASS;
    }
}
