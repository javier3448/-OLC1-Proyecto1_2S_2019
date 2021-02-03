/******************************************************************************/
/*-------------------------------H-T-M-L scnner-------------------------------*/
/******************************************************************************/

package ParserHTML;
import java_cup.runtime.*;
import MyObjects.MyError.MyErrorType;
import MyObjects.MyError.Phase;
import MyObjects.MyError;
import MyObjects.MyFileReader;


%%

//-----------------Options------------------
%public
%class Lexer
%ignorecase
%cup
/* someSymbol.left */
%line
/* someSymbol.left */
%column
%unicode

//------Member variables and functions------
%{
    private String path;
    private char auxChar;

    StringBuilder stringBuilder = new StringBuilder();

    //Helper functions to return multiple java_cup.runtime.Symbol to the parser
    private Symbol symbol(int type){
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }
    private Symbol symbol(int type, Object value){
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }

    public String getPath(){
        return path;
    }
%}

%init{
    //CHAPUZ MAXIMO se utilizo in en vez de zzReader porque el codigo dentro de init es copiado antes de hacer this.zzReader = in
    if(!(in instanceof MyFileReader)){
        throw new IllegalArgumentException("Reader in must be an instance of MyFileReader");
    }
    MyFileReader tmp = (MyFileReader) in;
    path = tmp.getPath();
%init}

%initthrow IllegalArgumentException

//-----------Regular expressions-----------
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]
_String = \"(\\.|[^\"\\])*\"

/* tags */
html_o = "<html" {WhiteSpace}* ">"
head_o = "<head" {WhiteSpace}* ">"
title_o = "<title" {WhiteSpace}* ">"
body_o = "<body" {WhiteSpace}* ">"
noufe_o = "<noufe" {WhiteSpace}* ">"
div_o = "<div" {WhiteSpace}+ "id" {WhiteSpace}* "=" {WhiteSpace}* {_String} {WhiteSpace}* ">"

html_c = "</html" {WhiteSpace}* ">"
head_c = "</head" {WhiteSpace}* ">"
title_c = "</title" {WhiteSpace}* ">"
body_c = "</body" {WhiteSpace}* ">"
noufe_c = "</noufe" {WhiteSpace}* ">"
div_c = "</div" {WhiteSpace}* ">"

nondef_tag = "<" ~">"

%state PLAIN_TEXT_STATE_0
%state PLAIN_TEXT_STATE_1

/* comments */
Comment = "<!--"  ~"-->"

%%
/* keywords */
<YYINITIAL> {html_o} { return symbol(sym.HTML_O); } 
<YYINITIAL> {head_o} { return symbol(sym.HEAD_O); } 
<YYINITIAL> {title_o} { return symbol(sym.TITLE_O); } 
<YYINITIAL> {body_o} { return symbol(sym.BODY_O); } 
<YYINITIAL> {noufe_o} { return symbol(sym.NOUFE_O); } 
<YYINITIAL> {div_o} { 
                        int substringIndex0 = yytext().indexOf('"');
                        int substringIndexF = yytext().lastIndexOf('"');
                        
                        return symbol(sym.DIV_O, yytext().substring(substringIndex0 + 1, substringIndexF)); 
                    } 

<YYINITIAL> {html_c} { return symbol(sym.HTML_C); } 
<YYINITIAL> {head_c} { return symbol(sym.HEAD_C); } 
<YYINITIAL> {title_c} { return symbol(sym.TITLE_C); } 
<YYINITIAL> {body_c} { return symbol(sym.BODY_C); } 
<YYINITIAL> {noufe_c} { return symbol(sym.NOUFE_C); }
<YYINITIAL> {div_c} { return symbol(sym.DIV_C); } 
<YYINITIAL> {
    /* literals */
    /* comments */
    {Comment} { /* ignore */ }
    {nondef_tag} { System.out.println("Tag no reconocida " + yytext());}
    /* whitespace */
    {WhiteSpace} { /* ignore */ }

    . {   
           auxChar = yytext().charAt(0); 
           stringBuilder.setLength(0); 
           stringBuilder.append(yytext());
           yypushback(1);
           stringBuilder.setLength(0);
           yybegin(PLAIN_TEXT_STATE_0); 
      }
}

<PLAIN_TEXT_STATE_0> {
    /* comments */
    {Comment} { /* ignore */ }
    /* whitespace */
    {WhiteSpace} { /* ignore */  } 
    
    "<"
    {
        yypushback(1);
        yybegin(YYINITIAL);
        String s = stringBuilder.toString().trim();
        if(s.length() > 0){
            return symbol(sym.PLAIN_TEXT, s); 
        }
    }

    .   
    {
        stringBuilder.append(yytext());
        yybegin(PLAIN_TEXT_STATE_1);
    }
}

<PLAIN_TEXT_STATE_1> {
    /* comments */
    {Comment} { /* ignore */ }
    /* whitespace */
    {WhiteSpace} 
    {
        stringBuilder.append(" ");
        yybegin(PLAIN_TEXT_STATE_0); 
    } 
    
    "<"
    {
        yypushback(1);
        yybegin(YYINITIAL);
        String s = stringBuilder.toString().trim();
        if(s.length() > 0){
            return symbol(sym.PLAIN_TEXT, s); 
        }
    }

    .   
    {
        stringBuilder.append(yytext());
    }
}

/* error fallback */
[^] { 
    MyError error = new MyError(MyErrorType.LEXIC, Phase.HTML, path, yyline + 1, yycolumn + 1, "Illegal character <"+ yytext()+">");
    MySystem.Console.println(error);
    //Pasar a consola
    //MyGui.MainJFrame.logln(error);
}

