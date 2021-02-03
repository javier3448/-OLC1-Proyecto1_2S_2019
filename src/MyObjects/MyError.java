/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyObjects;

/**
 *  PENDIENTE QUITAR EL toString();
 * @author Alvarez
 */
public class MyError {
    public Phase phase;
    public MyErrorType type;
    public String filePath;
    public int line;
    public int column;
    public String msg;
    
    public MyError(String msg) {
        this.type = MyErrorType.UNKNOWN;
        this.line = -1;
        this.column = -1;
        this.msg = msg;//CHAPUZ MAXIMO TEMPORAL, SE CONCATENO LA LINEA PARA FACILITAR EL DEBUGGING
    }
    
    public MyError(MyErrorType type, Phase phase, String filePath, int line, int column, String msg) {
        this.type = type;
        this.phase = phase;
        this.filePath = filePath;
        this.line = line;
        this.column = column;
        this.msg = msg;
    }
    
    public void set(MyErrorType type, Phase phase, String filePath, int line, int column){
        this.type = type;
        this.phase = phase;
        this.filePath = filePath;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return msg + ". linea: " + line + "\n    " + filePath; //Chapus temporal para ver la linea en la que ocurrio el error
    }
    
    
    public enum MyErrorType{
        UNKNOWN, LEXIC, SYNTAX, SEMANTIC;
        
        @Override
        public String toString() {
          switch(this) {
            case UNKNOWN: return "desconocido";
            case LEXIC: return "lexico";
            case SYNTAX: return "sintacitco";
            case SEMANTIC: return "semantico";
            default: throw new IllegalArgumentException();
          }
        }
    }
    public enum Phase{
        PREPROCESS, HTML, CSS, UFE, CSS_SYNTHETIZE, UFE_SYNTHETIZE, LINK;
        
        @Override
        public String toString() {
          switch(this) {
            case PREPROCESS: return "PREPROCESS";
            case HTML: return "HTML";
            case CSS: return "CSS";
            case UFE: return "UFE";
            case CSS_SYNTHETIZE: return "SINTETIZACION CSS";
            case UFE_SYNTHETIZE: return "SINTETIZACION UFE";
            case LINK: return "LINK";
            default: throw new IllegalArgumentException();
          }
        }
    }
}
