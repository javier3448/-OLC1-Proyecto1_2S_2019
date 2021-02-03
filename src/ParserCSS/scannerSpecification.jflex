/****************************************************************************/
/*------------------------------C-S-S scanner-------------------------------*/
/****************************************************************************/

package ParserCSS;
import java_cup.runtime.*;
import MyObjects.MyError.MyErrorType;
import MyObjects.MyError.Phase;
import MyObjects.MyError;
import MyObjects.MyFileReader;
import java.awt.Color;


%%

//-----------------Options------------------
%public
%class Lexer
%ignorecase
%cup
/* someSymbol.left */
%line
/* someSymbol.right */
%column
%unicode

//------Member variables and functions------
%{
    private String path;

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
InputCharacter = [^\r\n]
WhiteSpace = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment}
TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
                     | "/**" {CommentContent} "*"+ "/"
CommentContent = ( [^*] | \*+ [^/*] )*
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?

Number = -?[0-9]+
HexColor = #[0-9aA-fF]{6}
Identifier = [:jletter:] [:jletterdigit:]*
_String = ('(\\.|[^\'\\])*')

%%

/* keywords */
<YYINITIAL> "height" { return symbol(sym.HEIGHT); }
<YYINITIAL> "width" { return symbol(sym.WIDTH); }
<YYINITIAL> "background" { return symbol(sym.BACKGROUND); }
<YYINITIAL> "border" { return symbol(sym.BORDER); }
<YYINITIAL> "border-color" { return symbol(sym.BORDER_COLOR); }
<YYINITIAL> "border-width" { return symbol(sym.BORDER_WIDTH); }
<YYINITIAL> "font" { return symbol(sym.FONT); }
<YYINITIAL> "font-size" { return symbol(sym.FONT_SIZE); }
<YYINITIAL> "font-color" { return symbol(sym.FONT_COLOR); }
<YYINITIAL> "rgb" { return symbol(sym.RGB); }
<YYINITIAL> "align" { return symbol(sym.ALIGN); }
<YYINITIAL> {
/* literals */
    {_String} { return symbol(sym.STRING_LITERAL, yytext().replace("'", "")); }
    {Number} { return symbol(sym.NUMBER_LITERAL, Integer.valueOf(yytext())); }
    //Colors
    {HexColor} { return symbol(sym.HEX_COLOR, Integer.parseInt(yytext().substring(1).toLowerCase(), 16)); }
    "red" { return symbol(sym.COLOR_NAME, new Integer(0xff0000)); } //CHAPUZ MINIMO DEBERIA DE RETORNAR EL COLOR NO EL INTEGER RGB DEL COLOR
    "green" { return symbol(sym.COLOR_NAME, new Integer(0x00ff00)); }
    "blue" { return symbol(sym.COLOR_NAME, new Integer(0x0000ff)); }
    "pink" { return symbol(sym.COLOR_NAME, new Integer(Color.pink.getRGB())); }
    "orange" { return symbol(sym.COLOR_NAME, new Integer(Color.orange.getRGB())); }
    "yellow" { return symbol(sym.COLOR_NAME, new Integer(Color.yellow.getRGB())); }
    "purple" { return symbol(sym.COLOR_NAME, new Integer(0x800080)); }
    "magenta" { return symbol(sym.COLOR_NAME, new Integer(Color.magenta.getRGB())); }
    "brown" { return symbol(sym.COLOR_NAME, new Integer(0xa52a2a)); }
    "white" { return symbol(sym.COLOR_NAME, new Integer(Color.white.getRGB())); }
    "gray" { return symbol(sym.COLOR_NAME, new Integer(Color.gray.getRGB())); }
    "black" { return symbol(sym.COLOR_NAME, new Integer(Color.black.getRGB())); }
    //Booleans
    "true" { return symbol(sym.BOOL_LITERAL, new Boolean(true)); }
    "false" { return symbol(sym.BOOL_LITERAL, new Boolean(false)); }
    //Aligns
    "left" { return symbol(sym.ALIGN_LITERAL, new Integer(javax.swing.SwingConstants.LEFT)); }
    "right" { return symbol(sym.ALIGN_LITERAL, new Integer(javax.swing.SwingConstants.RIGHT)); }
    "center" { return symbol(sym.ALIGN_LITERAL, new Integer(javax.swing.SwingConstants.CENTER)); }
    /* non-literals */
    {Identifier} { return symbol(sym.IDENTIFIER, yytext()); }
    /* operators llaves y separators */
    "(" { return symbol(sym.O_PAREN); }
    ")" { return symbol(sym.C_PAREN); }
    ";" { return symbol(sym.SEMICOLON); }
    "," { return symbol(sym.COMMA); }
    "{" { return symbol(sym.O_CURLY); }
    "}" { return symbol(sym.C_CURLY); }
    "." { return symbol(sym.DOT); }
    ":" { return symbol(sym.COLON); }

    /* comments */
    {Comment} { /* ignore */ }
    /* whitespace */
    {WhiteSpace} { /* ignore */ }
}
/* error fallback */
[^] { 
    MyError error = new MyError(MyErrorType.LEXIC, Phase.CSS, path, yyline + 1, yycolumn + 1, "Illegal character <"+ yytext()+">");
    MySystem.Console.println((MyError)error);
    //Pasar a consola
    //MyGui.MainJFrame.logln(error);
}