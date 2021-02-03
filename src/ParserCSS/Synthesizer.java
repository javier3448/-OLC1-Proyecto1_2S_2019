/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ParserCSS;

import MyObjects.MyError;
import MyObjects.MyError.MyErrorType;
import MyObjects.MyError.Phase;
import ParserCSS.CssNode.NodeTag;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Alvarez
 */
public class Synthesizer {
    
    private Synthesizer(){}
    
    public static Synthesizer instance = new Synthesizer();
    
    public static Synthesizer getInstance(){
        return instance;
    }
    
    //ARREGLAR PARA VERSION FINAL! POR AHORA ES IMPOSIBLE QUE RETORNE UN MyError POR SI SE HACEN CAMBIOS Y SEA POSIBLE QUE EL PROCESO DE SINTETIZACION DE ALGUN ERROR.
    public Object synthesizeCss(CssNode root){
        //CHAPUZ MEDIO ALTO: el .value del primer nodo l_class va a contener un String con el valor del path del documento!
        if (root.getTag() != NodeTag.L_CLASS) {
            String msg = "synthesizeCss(CssNode): root.tag debe ser igual a l_class";
            return new MyError(MyErrorType.SEMANTIC, Phase.CSS_SYNTHETIZE, root.getValue().toString(), root.getLine(), root.getColumn(), msg);
        }
        
        CssHelper cssHelper = new CssHelper(root.getValue().toString());
        
        MyError error = traverseCssClass(root, cssHelper);
        
        if (error != null) {
            return error;
        }
        
        return cssHelper;
    }
    
    //traverse a todos los nodos l_class class
    //Retorna si hubo error semantico al recorrer el nodo root
    private MyError traverseCssClass(CssNode root, CssHelper container/*puede ser MyError o el CssHelper resultado*/){
        
        MyError result = null;
        
        switch (root.getTag()) {
            case L_CLASS: //Recorrido en post orden
                for (CssNode child : root.getChildren()) {
                    result =traverseCssClass(child, container);
                    //Si hubo error mas adelante en la recursividad
                    if(result != null){
                        return result;
                    }
                }
                break;
            case CLASS: //Recorrido en pre orden
                //Genera la clase en el cssHelper
                SimpleEntry<String, HashMap<Integer, Object>> pair = (SimpleEntry<String, HashMap<Integer, Object>>) root.getValue();
                
                String className = pair.getKey();
                HashMap<Integer, Object> classProperties = pair.getValue();
                
                container.add(className, classProperties);
                
                for (CssNode child : root.getChildren()) {
                    result = traverseCssSubclass(child, container, className);
                    if(result != null){
                        return result;
                    }
                }
                
                return null;
            default:
                throw new AssertionError("tag no valida: <" + root.getTag().toString() + "> en traverser class");
        }
        return null;
    }
    
    //className nombre de la clase padre. root debe ser de tag l_subclass o subclass
    private MyError traverseCssSubclass(CssNode root, CssHelper container, String className){
        
        MyError result = null;
        
        switch (root.getTag()) {
            case L_SUBCLASS: //Recorrido en post orden
                for (CssNode child : root.getChildren()) {
                    result = traverseCssSubclass(child, container, className);
                    if (result != null) {
                        return result;
                    }
                }
                return null;
            case SUBCLASS: //Agregar a cssHelper
                //Genera la clase en el cssHelper
                SimpleEntry<String, HashMap<Integer, Object>> pair = (SimpleEntry<String, HashMap<Integer, Object>>) root.getValue();
                
                String subclassName = pair.getKey();
                HashMap<Integer, Object> subclassProperties = pair.getValue();
                
                container.add(className + " " + subclassName, subclassProperties);
                return null;
            default:
                throw new AssertionError("tag no valida: <" + root.getTag().toString() + "> en csstraverser subclass");
        }
    }
    
    /**
     * SOLO PARA DEBUGGING
     * @param root
     * @return 
     */
    public LinkedList<String> generateGraphviz(CssNode root){
        LinkedList<String> lines = new LinkedList<String>();
        lines.add("digraph CssAstTree{");
        lines.add("node[shape = record];");
        lines.add("style=invis;");
        
        generateGraphviz(lines, root);
        
        lines.add("}");
        
        for(String line : lines){
            System.out.println(line);
        }
        
        return lines;
    }

    /**
     * SOLO PARA DEBUGGING
     * @param container
     * @param node 
     */
    private void generateGraphviz(LinkedList<String> container, CssNode node) {
        if (node == null){
            return;
        }
        String val = "";
        if (node.getValue() != null) {
            if (node.getValue() instanceof SimpleEntry) {
                SimpleEntry<String, HashMap<String, Object>> entry = (SimpleEntry<String, HashMap<String, Object>>) node.getValue();
                val = entry.getKey() + " \\n" + entry.getValue().toString().replace("{", "\\{").replace("}", "\\}");
            }
            else{
                val = node.getValue().toString();
            }
        }
        container.add(node.toString().replace(".", "").replace("@", "") + "[ label = \"" + node.getTag().toString() + "\\n" + val + "\" ];");
        for (CssNode child : node.getChildren()) {
            generateGraphviz(container, child);
            container.add(node.toString().replace(".", "").replace("@", "") + "->" + child.toString().replace(".", "").replace("@", "") + ";");
        }
    }
}
