/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ParserCSS;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 * Depende de ParserCSS.sym
 * @author Alvarez
 */
public class UfeComponentStylizer {
    //setBackGround
    private Color bgColor = null;
    //setBorder
    private Boolean hasBorder = null;
    private Color borderColor = null;
    private Integer borderWidth = null;
    private static final int MY_FONT_DEFAULT_STYLE = Font.PLAIN;
    //setHorizontalAligment (for JTextField and JLabel)
    private Integer align = null;
    //setFont
    private String fontName = null;
    private Integer fontSize = null;
    //setForeground
    private Color fontColor = null;
    //setBounds
    private Integer width = 0;
    private Integer height = 0;
    
    public UfeComponentStylizer(HashMap<Integer, Object> properties){
        //setBackGround
        bgColor = (Color) properties.get(ParserCSS.sym.BACKGROUND);
        //setBorder
        hasBorder = (Boolean) properties.get(ParserCSS.sym.BORDER);
        borderColor = (Color) properties.get(ParserCSS.sym.BORDER_COLOR);
        borderWidth = (Integer) properties.get(ParserCSS.sym.BORDER_WIDTH);
        //setHorizontalAligment
        align = (Integer) properties.get(ParserCSS.sym.ALIGN);
        //setFont
        fontName = (String) properties.get(ParserCSS.sym.FONT);
        fontSize = (Integer) properties.get(ParserCSS.sym.FONT_SIZE);
        //setForeground
        fontColor = (Color) properties.get(ParserCSS.sym.FONT_COLOR);
        //setBounds
        width = (Integer) properties.get(ParserCSS.sym.WIDTH);
        height = (Integer) properties.get(ParserCSS.sym.HEIGHT);
    }
    
    public void stylize(JComponent ufeComponent){
        //setBackGround
        if (bgColor != null) {
            if (ufeComponent instanceof JLabel) {
                ufeComponent.setOpaque(true);
            }
            ufeComponent.setBackground(bgColor);
        }
        //setBorder
        Color newBorderColor = null;
        Integer newBorderWidth = null;
        boolean hadBorder = ufeComponent.getBorder() instanceof LineBorder;
        
        //CHAPUZ MAXIMO DE TODOS LOS TIEMPOS, NUNCA HABIA HECHO UN CHAPUZ TAN GRANDE PARA MANTENER Y REEMPLAZAR LOS VALORES ANTERIORES
        if (hasBorder == null) {
            if (borderColor != null || borderWidth != null) {
                if (borderColor != null) {
                    newBorderColor = borderColor;
                }
                else if(hadBorder){
                    newBorderColor = ((LineBorder) ufeComponent.getBorder()).getLineColor();
                }
                else{
                    newBorderColor = Color.BLACK;
                }
                
                if (borderWidth != null) {
                    newBorderWidth = borderWidth;
                }
                else if (hadBorder) {
                    newBorderWidth = ((LineBorder) ufeComponent.getBorder()).getThickness();
                }
                else{
                    newBorderWidth = 1;
                }
                ufeComponent.setBorder(new LineBorder(newBorderColor, newBorderWidth));
            }
        }
        else if(hasBorder){
            if (borderColor != null) {
                newBorderColor = borderColor;
            }
            else if(hadBorder){
                newBorderColor = ((LineBorder) ufeComponent.getBorder()).getLineColor();
            }
            else{
                newBorderColor = Color.BLACK;
            }

            if (borderWidth != null) {
                newBorderWidth = borderWidth;
            }
            else if (hadBorder) {
                newBorderWidth = ((LineBorder) ufeComponent.getBorder()).getThickness();
            }
            else{
                newBorderWidth = 1;
            }
            ufeComponent.setBorder(new LineBorder(newBorderColor, newBorderWidth));
        }
        else{
            ufeComponent.setBorder(null);
        }
        
        //Set align
        if (align != null) {
            if (ufeComponent instanceof JTextField) {
                ((JTextField) ufeComponent).setHorizontalAlignment(align);
            }
            else if (ufeComponent instanceof JLabel) {
                ((JLabel) ufeComponent).setHorizontalAlignment(align);
            }
        }
        
        //Set font
        String newFontName = ufeComponent.getFont().getName();
        int newFontSize = ufeComponent.getFont().getSize();
        if (fontName != null || fontSize != null) {
            if (fontName != null) {
                newFontName = fontName;
            }
            if (fontSize != null) {
                newFontSize = fontSize.intValue();
            }
            ufeComponent.setFont(new Font(newFontName, MY_FONT_DEFAULT_STYLE, newFontSize));
        }
        
        //setForeground
        if (fontColor != null) {
            ufeComponent.setForeground(fontColor);
        }
        
        //setBounds
        int newWidth = ufeComponent.getBounds().width;
        int newHeight = ufeComponent.getBounds().height;
        if (width != null) {
            newWidth = width;
        }
        if (height != null) {
            newHeight = height;
        }
        ufeComponent.setBounds(new Rectangle(ufeComponent.getBounds().x,ufeComponent.getBounds().y, newWidth, newHeight)); 
    }
}
