/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeNodes;

import UfeInterfaces.AstNode;
import UfeInterfaces.UfeVisitor;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Alvarez
 */
public abstract class Ufex extends AstNode{
    
    //Component
    public static abstract class Comp extends Ufex{
        
        public static abstract class Default extends Comp{
            public HashMap<PropType, PropValue> properties;
            
            public static class Panel extends Default{

                public LinkedList<Comp> components;

                public Panel(HashMap<PropType, PropValue> properties, LinkedList<Comp> components) {
                    this.properties = properties;
                    this.components = components;
                }

                @Override
                public Object accept(UfeVisitor v) {
                    
                    return v.visit(this);
                }

            }
            public static class Text extends Default{

                public CntGroup contentGroup;

                public Text(HashMap<PropType, PropValue> properties, CntGroup contentGroup) {
                    this.properties = properties;
                    this.contentGroup = contentGroup;
                }

                @Override
                public Object accept(UfeVisitor v) {
                    return v.visit(this);
                }

            }
            public static class TextField extends Default{

                public CntGroup contentGroup;

                public TextField(HashMap<PropType, PropValue> properties, CntGroup contentGroup) {
                    super.properties = properties;
                    this.contentGroup = contentGroup;
                }



                @Override
                public Object accept(UfeVisitor v) {
                    return v.visit(this);
                }

            }
            public static class Button extends Default{

                public CntGroup contentGroup;

                public Button(HashMap<PropType, PropValue> properties, CntGroup contentGroup) {
                    this.properties = properties;
                    this.contentGroup = contentGroup;
                }

                @Override
                public Object accept(UfeVisitor v) {
                    return v.visit(this);
                }

            }
            public static class List extends Default{

                public LinkedList<CntGroup> items;
                public CntGroup _default;

                public List(HashMap<PropType, PropValue> properties, LinkedList<CntGroup> items, CntGroup _default) {
                    this.properties = properties;
                    this.items = items;
                    this._default = _default;
                }

                @Override
                public Object accept(UfeVisitor v) {
                    return v.visit(this);
                }

            }

            public static class Spinner extends Default{//Solo spinner no tiene CntGroup porque El contenido de Spinner debe ser un solo numero

                public Cnt _default;

                public Spinner(HashMap<PropType, PropValue> properties, Cnt _default) {
                    this.properties = properties;
                    this._default = _default;
                }

                @Override
                public Object accept(UfeVisitor v) {
                    return v.visit(this);
                }

            }
            public static class Image extends Default{

                public Image(HashMap<PropType, PropValue> properties) {
                    this.properties = properties;
                }

                @Override
                public Object accept(UfeVisitor v) {
                    return v.visit(this);
                }

            }
        }
        
        public static class Custom extends Comp{
            public String id;

            public Custom(String id) {
                this.id = id;
            }
            
            @Override
            public Object accept(UfeVisitor v) {
                return v.visit(this);
            }
            
        }
    }
    
    //Lista de contenidos. contenido puede ser plain text of embedded ufe
    public static class CntGroup extends Ufex{

        public LinkedList<Cnt> contents;

        public CntGroup(LinkedList<Cnt> contents) {
            this.contents = contents;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
        
    }
    
    public static abstract class Cnt extends Ufex{
        public static class PlainTxt extends Cnt{

            public String txt;

            public PlainTxt(String txt) {
                this.txt = txt;
            }
            
            
            
            @Override
            public Object accept(UfeVisitor v) {
                return v.visit(this);
            }
            
        }
        
        //Embedded ufex
        public static class EmbUfe extends Cnt{
            
            public Expr expr;

            public EmbUfe(Expr expr) {
                this.expr = expr;
            }
            
            @Override
            public Object accept(UfeVisitor v) {
                return v.visit(this);
            }
        }
    }
    
    //Chapuz medio alto para diferenciar entre embedded ufe de prop y embedded ufe de cnt. Investigar jerarquias para arreglar esto
    public static abstract class PropValue extends Ufex{
        public static class IdProp extends PropValue{

            public String id;

            public IdProp(String id) {
                this.id = id;
            }
            
            @Override
            public Object accept(UfeVisitor v) {
                return v.visit(this);
            }
        }
        
        public static class AtomicProp extends PropValue{
            public Object value;

            public AtomicProp(Object value) {
                this.value = value;
            }
            
            @Override
            public Object accept(UfeVisitor v) {
                return v.visit(this);
            }
        }
        
        public static class EmbProp extends PropValue{

            public Expr expr;

            public EmbProp(Expr expr) {
                this.expr = expr;
            }
            
            @Override
            public Object accept(UfeVisitor v) {
                return v.visit(this);
            }
            
        }
    }
   
    public enum CompType{
        PANEL, TEXT, TEXT_FIELD, BUTTON, LIST, SPINNER, IMAGE, CUSTOM;
    }
    
    public enum PropType{
        ID, X, Y, HEIGHT, WIDTH, COLOR, BORDER, CLASSNAME, ONCLICK, MIN, MAX, SRC;
        /**
         * Si es un atributo comun de ufe
         * @return 
         */
        public boolean isDefault(){
            switch (this) {
                case ID:
                case X:
                case Y:
                case HEIGHT:
                case WIDTH:
                case COLOR:
                case BORDER:
                case CLASSNAME:
                    return true;
                case ONCLICK:
                case MIN:
                case MAX:
                case SRC:
                    return true;
                default:
                    throw new AssertionError();
            }
        }
    }
}
