/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeEnvironment;

import MyObjects.MyError;
import ParserCSS.CssHelper;
import ParserCSS.UfeComponentStylizer;
import UfeNodes.Ufex.CompType;
import UfeNodes.Ufex.PropType;
import java.awt.Color;
import java.util.HashMap;
import java.util.function.BiConsumer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.border.LineBorder;

/**
 * agrupa los metodos para crear cada componente
 * @author Alvarez
 */
public class CompFactory {
    
    private CompFactory(){}
    
    /**
     * Creo un nuevo componente seteando las properties default (id, x, y, height, width, Color, border)
     * @param type
     * @param properties
     * @param stylizer puede ser null
     * @return nueva instancia de componente
     */
    public static JComponent getComp(CompType type, HashMap<PropType, Object> properties){
        JComponent comp = null;
        switch (type) {
            case PANEL:
                comp = new JPanel();
                comp.setLayout(null);
                break;
            case TEXT:
                comp = new JLabel();
                break;
            case TEXT_FIELD:
                comp = new JTextField();
                break;
            case BUTTON:
                comp = new JButton();
                break;
            case LIST:
                comp = new JComboBox();
                break;
            case SPINNER:
                comp = new JSpinner();
                break;
            case IMAGE:
                comp = new JLabel();
                break;
            case CUSTOM:
                throw new AssertionError("UfeCompFactory no puede fabricar un componente Custom");
            default:
                throw new AssertionError("CompType no valido");
        }
        
        
        
    //Setea properties
        int x = (Integer) properties.getOrDefault(PropType.X, Default.X);
        int y = (Integer) properties.getOrDefault(PropType.Y, Default.Y);
        int width = (Integer) properties.getOrDefault(PropType.WIDTH, Default.WIDTH);
        int height = (Integer) properties.getOrDefault(PropType.HEIGHT, Default.HEIGHT);
        
        comp.setBounds(x, y, width, height);
        
        //Caso especial para que el texto tenga fondo transparente por default
        Object bgColor = properties.get(PropType.COLOR);
        if (bgColor == null) {
            bgColor = Default.COLOR;
        }
        else if (type == CompType.TEXT) {
            comp.setOpaque(true);
        }
        
        if (bgColor instanceof String) {
            String colorName = (String) bgColor; 
            bgColor = ColorFactory.getColor(colorName);
            if (bgColor == null) {
                bgColor = Default.COLOR;
                MyError error = new MyError("Color string: " + colorName + " no reconocido, se uso el color default");//Chapuz. no deberia de reportar el error desde aqui. Pero fue lo ultimo que se agrego
                MySystem.Console.println(error);
            }
        }else if(bgColor instanceof Integer){
            bgColor = new Color((Integer) bgColor);
        }
        comp.setBackground((Color) bgColor);
        
        Integer lineThickness = (Integer) properties.get(PropType.BORDER);
        if (lineThickness != null) {
            comp.setBorder(new LineBorder(Default.LINE_COLOR, lineThickness));
        }
        
        comp.setName((String) properties.getOrDefault(PropType.ID, Default.ID));
        
        return comp;
    }
    
    private static class Default{
        private static final int X = 0;
        private static final int Y = 0;
        private static final int HEIGHT = 100;
        private static final int WIDTH = 100;
        private static final Color COLOR = Color.WHITE;
        private static final Color LINE_COLOR = Color.BLACK;
        private static final String ID = "UNKNOWN";
        //private static final int BORDER = 0;//Border no es necesario porque no se tiene que hacer nada
    }
    
    private static class ColorFactory{
        private static Color getColor(String colorName){
            colorName = colorName.toLowerCase();
            switch (colorName) {
                case "red":
                    return Color.RED;
                case "pink":
                    return Color.PINK;
                case "orange":
                    return Color.ORANGE;
                case "yellow":
                    return Color.YELLOW;
                case "purple":
                    return new Color(0x800080);
                case "magenta":
                    return Color.MAGENTA;
                case "green":
                    return Color.GREEN;
                case "blue":
                    return Color.BLUE;
                case "brown":
                    return new Color(0xa52a2a);
                case "white":
                    return Color.WHITE;
                case "gray":
                    return Color.GRAY;
                case "black":
                    return Color.BLACK;
                default:
                    return null;
            }
        }
    }
}
