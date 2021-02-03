/****************************************************************************/
/*------------------------------U-F-E scanner-------------------------------*/
/****************************************************************************/

package ParserUFE;
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
/* someSymbol.right */
%column
%unicode

//------Member variables and functions------
%{
    private String path;

    StringBuilder stringBuilder = new StringBuilder();
    char tmpChar = 0;

    /**
    * variable que sirve para regresar de STRING_STATE al estado que lo inicio
    */
    int prevState_String = YYINITIAL;

    /**
    * variable que sirve para regresar de EMBEDDED_UFE_STATE al estado que lo inicio
    */
    int prevState_EmbeddedUfe = YYINITIAL; 

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

IntegerRegex = [0-9]+
DoubleRegex = [0-9]+(\.[0-9]+)?
CharacterRegex = '.'
Identifier = [:jletter:] [:jletterdigit:]*

HexColor = #[0-9aA-fF]{6}

/* tags */
identifier_o = "<" {Identifier}

panel_o = "<Panel"
text_o = "<Text"
textfield_o = "<TextField"
button_o = "<Button"
list_o = "<List"
spinner_o = "<Spinner"
image_o = "<Image"
elements_o = "<Elements"
item_o = "<item"
default_o = "<default"
identifier_o = "<" {Identifier}

panel_c = "</Panel"
text_c = "</Text"
textfield_c = "</TextField"
button_c = "</Button"
list_c = "</List"
spinner_c = "</Spinner"
image_c = "</Image"
elements_c = "</Elements"
item_c = "</item"
default_c = "</default"
identifier_c = "</" {Identifier}

//plainText0 = ("}" | ">") ~("<" "/"? {Identifier})
//plainText1 = ("}" | ">") ~({embeddedUfe} | "<" "/"? {Identifier}) //Plain text antes de embedded

%state STRING_STATE

%state UFEX_INITIAL, UFEX_TAG, UFEX_PROPERTIES, UFEX_PLAINTEXT_0, UFEX_PLAINTEXT_1, PRE_EMBEDDED_UFE_STATE, EMBEDDED_UFE_STATE

%%


<YYINITIAL> {
/* keywords */
    "var" { return symbol(sym.VAR); }   
    "render" { return symbol(sym.RENDER); }
    "return" { yybegin(UFEX_INITIAL); return symbol(sym.RETURN); }
    "component" { return symbol(sym.COMPONENT); }
    "import" { return symbol(sym.IMPORT); }
    "from" { return symbol(sym.FROM); }
    "si" { return symbol(sym.SI); }
    "sino" { return symbol(sym.SINO); }
    "repetir" { return symbol(sym.REPETIR); }
    "mientras" { return symbol(sym.MIENTRAS); }
    "imprimir" { return symbol(sym.IMPRIMIR); }
    "pow" { return symbol(sym.POW); }

/* literals */
    \" { stringBuilder.setLength(0); prevState_String = YYINITIAL; yybegin(STRING_STATE); }
    {IntegerRegex} { return symbol(sym.INTEGER_LITERAL, Integer.valueOf(yytext())); }
    {DoubleRegex} { return symbol(sym.DOUBLE_LITERAL, Double.valueOf(yytext())); }
    {CharacterRegex} { return symbol(sym.CHARACTER_LITERAL, Character.valueOf(yytext().charAt(1))); }
    //BOOLEAN_LITERALS
    "true" { return symbol(sym.BOOLEAN_LITERAL, new Boolean(true)); }
    "false" { return symbol(sym.BOOLEAN_LITERAL, new Boolean(false)); }
    /* non-literals */
    {Identifier} { return symbol(sym.IDENTIFIER, yytext()); }
    /* operators llaves y separators */
    "(" { return symbol(sym.O_PAREN); }
    ")" { return symbol(sym.C_PAREN); }
    "{" { return symbol(sym.O_CURLY); }
    "}" { return symbol(sym.C_CURLY); }
    "[" { return symbol(sym.O_BOX); }
    "]" { return symbol(sym.C_BOX); }
    "," { return symbol(sym.COMMA); }
    ";" { return symbol(sym.SEMICOLON); }
    "=" { return symbol(sym.EQ); }
    "+" { return symbol(sym.PLUS); }
    "-" { return symbol(sym.MINUS); }
    "*" { return symbol(sym.MULT); }
    "/" { return symbol(sym.DIV); }
    "<" { return symbol(sym.LESS); }
    ">" { return symbol(sym.GREATER); }
    "<=" { return symbol(sym.LESS_EQ); }
    ">=" { return symbol(sym.GREATER_EQ); }
    "==" { return symbol(sym.EQ_EQ); }
    "!=" { return symbol(sym.NOT_EQ); }
    "&&" { return symbol(sym.AND); }
    "||" { return symbol(sym.OR); }
    "^" { return symbol(sym.XOR); }
    "!" { return symbol(sym.NOT); }

    /* comments */
    {Comment} { /* ignore */ }
    /* whitespace */
    {WhiteSpace} { /* ignore */ }
}

<STRING_STATE> {
    \" { yybegin(prevState_String);
    return symbol(sym.STRING_LITERAL,
    stringBuilder.toString()); }
    [^\n\r\"\\]+ { stringBuilder.append( yytext() ); }
    \\t { stringBuilder.append('\t'); }
    \\n { stringBuilder.append('\n'); }
    \\r { stringBuilder.append('\r'); }
    \\\" { stringBuilder.append('\"'); }
    \\\\ { stringBuilder.append('\\'); }
}
<UFEX_INITIAL> {
    "(" { yybegin(UFEX_TAG); return symbol(sym.O_PAREN); }//Retornar normal e ingresar a UFEX_TAG
    {WhiteSpace} { /* ignore */}
}
//Busca todos los posibles inicios a un tag < o tag </
<UFEX_TAG> {
/* keywords */
    {panel_o} { yybegin(UFEX_PROPERTIES); return symbol(sym.PANEL_O); }
    {text_o} { yybegin(UFEX_PROPERTIES); return symbol(sym.TEXT_O); }
    {textfield_o} { yybegin(UFEX_PROPERTIES); return symbol(sym.TEXTFIELD_O); }
    {button_o} { yybegin(UFEX_PROPERTIES); return symbol(sym.BUTTON_O); }
    {list_o} { yybegin(UFEX_PROPERTIES); return symbol(sym.LIST_O); }
    {spinner_o} { yybegin(UFEX_PROPERTIES); return symbol(sym.SPINNER_O); }
    {image_o} { yybegin(UFEX_PROPERTIES); return symbol(sym.IMAGE_O); }
    {elements_o} { yybegin(UFEX_PROPERTIES); return symbol(sym.ELEMENTS_O); }
    {item_o} { yybegin(UFEX_PROPERTIES); return symbol(sym.ITEM_O); }
    {default_o} { yybegin(UFEX_PROPERTIES); return symbol(sym.DEFAULT_O); }
    {identifier_o} { yybegin(UFEX_PROPERTIES); return symbol(sym.CUSTOM_O, yytext().substring(1)); }
    {panel_c} { yybegin(UFEX_PROPERTIES); return symbol(sym.PANEL_C); }
    {text_c} { yybegin(UFEX_PROPERTIES); return symbol(sym.TEXT_C); }
    {textfield_c} { yybegin(UFEX_PROPERTIES); return symbol(sym.TEXTFIELD_C); }
    {button_c} { yybegin(UFEX_PROPERTIES); return symbol(sym.BUTTON_C); }
    {list_c} { yybegin(UFEX_PROPERTIES); return symbol(sym.LIST_C); }
    {spinner_c} { yybegin(UFEX_PROPERTIES); return symbol(sym.SPINNER_C); }
    {image_c} { yybegin(UFEX_PROPERTIES); return symbol(sym.IMAGE_C); }
    {elements_c} { yybegin(UFEX_PROPERTIES); return symbol(sym.ELEMENTS_C); }
    {item_c} { yybegin(UFEX_PROPERTIES); return symbol(sym.ITEM_C); }
    {default_c} { yybegin(UFEX_PROPERTIES); return symbol(sym.DEFAULT_C); }
    {identifier_c} { yybegin(UFEX_PROPERTIES); return symbol(sym.CUSTOM_C, yytext().substring(2)); }
/* Separators */
    ")" { yybegin(YYINITIAL); return symbol(sym.C_PAREN); }

    {WhiteSpace} { /* ignore */ }
}
//Retorna todas las palabras reservadas y literales relacionadas a las propiedades de un tag
<UFEX_PROPERTIES> {
    "id" { return symbol(sym.ID); }
    "x" { return symbol(sym.X); }
    "y" { return symbol(sym.Y); }
    "height" { return symbol(sym.HEIGHT); }
    "width" { return symbol(sym.WIDTH); }
    "color" { return symbol(sym.COLOR); }
    "border" { return symbol(sym.BORDER); }
    "classname" { return symbol(sym.CLASSNAME); }
    "onclick" { return symbol(sym.ONCLICK); }
    "min" { return symbol(sym.MIN); }
    "max" { return symbol(sym.MAX); }
    "src" { return symbol(sym.SRC); }
/* literals */
    \" 
    { 
        stringBuilder.setLength(0); 
        prevState_String = UFEX_PROPERTIES; 
        yybegin(STRING_STATE); 
    }
    {IntegerRegex} { return symbol(sym.INTEGER_LITERAL, Integer.valueOf(yytext())); }
    {HexColor}  { return symbol(sym.COLOR_LITERAL, Integer.parseInt(yytext().substring(1).toLowerCase(), 16)); }
    "{" 
    { 
        yypushback(1); 
        prevState_EmbeddedUfe = UFEX_PROPERTIES; 
        yybegin(PRE_EMBEDDED_UFE_STATE); 
    }
/* non-literals */
    {Identifier} { return symbol(sym.IDENTIFIER, yytext()); }
/* separators and operators*/
    "-" { return symbol(sym.MINUS); }
    "=" { return symbol(sym.EQ); }
    ">" { tmpChar = 0; stringBuilder.setLength(0); yybegin(UFEX_PLAINTEXT_0); return symbol(sym.GREATER); }//hacer pushback a > y empezar a buscar PLAINTEXT

    {WhiteSpace} {/* ignore */}
}
<UFEX_PLAINTEXT_0> {
    {WhiteSpace} { /* ignore */ }
    "<" "/"? {Identifier}
    {
        yypushback(yylength());
        yybegin(UFEX_TAG);
        String s = stringBuilder.toString().trim();
        if(s.length() > 0){
            return symbol(sym.PLAIN_TEXT, stringBuilder.toString().replaceAll("[\r|\n| |\t|\f]+", " "));//Chapuz alto para quitar los espacios extra
        }
    }
    "\\)" 
    { 
        if(tmpChar != 0){
            stringBuilder.append(tmpChar); 
        }
        stringBuilder.append(")"); 
        yybegin(UFEX_PLAINTEXT_1);
    }
    ")" 
    {
        yypushback(1);
        yybegin(YYINITIAL);
        String s = stringBuilder.toString().trim();
        if(s.length() > 0){
            return symbol(sym.PLAIN_TEXT, stringBuilder.toString());
        }
    }
    "{" 
    {
        prevState_EmbeddedUfe = UFEX_PLAINTEXT_1;
        yypushback(1);
        yybegin(PRE_EMBEDDED_UFE_STATE);
        if(tmpChar != 0){
            stringBuilder.append(tmpChar);
        }
        tmpChar = 0;
        String s = stringBuilder.toString().trim();
        if(s.length() > 0){
            return symbol(sym.PLAIN_TEXT, stringBuilder.toString());
        }
    }
    . 
    { 
        if(tmpChar != 0){
            stringBuilder.append(tmpChar);
        }
        stringBuilder.append(yytext());
        yybegin(UFEX_PLAINTEXT_1);
    }
}
<UFEX_PLAINTEXT_1> {
    {WhiteSpace} 
    { 
        tmpChar = ' '; 
        yybegin(UFEX_PLAINTEXT_0); 
    }
    "<" "/"? {Identifier}
    {
        yypushback(yylength());
        yybegin(UFEX_TAG);
        String s = stringBuilder.toString().trim();
        if(s.length() > 0){
            return symbol(sym.PLAIN_TEXT, stringBuilder.toString());
        }
    }
    "\\)" 
    { 
        if(tmpChar != 0){
            stringBuilder.append(tmpChar);
        }
        stringBuilder.append(")"); 
    }
    ")" 
    {
        yypushback(1);
        yybegin(YYINITIAL);
        String s = stringBuilder.toString().trim();
        if(s.length() > 0){
            return symbol(sym.PLAIN_TEXT, stringBuilder.toString());
        }
    }
    "{" 
    {
        yypushback(1);
        prevState_EmbeddedUfe = UFEX_PLAINTEXT_1;
        yybegin(PRE_EMBEDDED_UFE_STATE);
        String s = stringBuilder.toString().trim();
        if(s.length() > 0){
            return symbol(sym.PLAIN_TEXT, stringBuilder.toString());
        }
    }
    . 
    { 
        stringBuilder.append(yytext());
    }
}
<PRE_EMBEDDED_UFE_STATE>{
    "{"
    {
        yybegin(EMBEDDED_UFE_STATE);
        return symbol(sym.O_CURLY);
    }
}
//Embedded, busca todo lo que puede venir en una expr, cuando encuentra "}" termina y se va a otro estado
<EMBEDDED_UFE_STATE> {
    "pow" { return symbol(sym.POW); }

/* literals */
    \" { stringBuilder.setLength(0); prevState_String = EMBEDDED_UFE_STATE; yybegin(STRING_STATE); }
    {IntegerRegex} { return symbol(sym.INTEGER_LITERAL, Integer.valueOf(yytext())); }
    {DoubleRegex} { return symbol(sym.DOUBLE_LITERAL, Double.valueOf(yytext())); }
    {CharacterRegex} { return symbol(sym.CHARACTER_LITERAL, Character.valueOf(yytext().charAt(1))); }
    //BOOLEAN_LITERALS
    "true" { return symbol(sym.BOOLEAN_LITERAL, new Boolean(true)); }
    "false" { return symbol(sym.BOOLEAN_LITERAL, new Boolean(false)); }
    /* non-literals */
    {Identifier} { return symbol(sym.IDENTIFIER, yytext()); }
    /* operators llaves y separators */
    "(" { return symbol(sym.O_PAREN); }
    ")" { return symbol(sym.C_PAREN); }
    "[" { return symbol(sym.O_BOX); }
    "]" { return symbol(sym.C_BOX); }
    "+" { return symbol(sym.PLUS); }
    "-" { return symbol(sym.MINUS); }
    "*" { return symbol(sym.MULT); }
    "/" { return symbol(sym.DIV); }
    "<" { return symbol(sym.LESS); }
    ">" { return symbol(sym.GREATER); }
    "<=" { return symbol(sym.LESS_EQ); }
    ">=" { return symbol(sym.GREATER_EQ); }
    "==" { return symbol(sym.EQ_EQ); }
    "!=" { return symbol(sym.NOT_EQ); }
    "&&" { return symbol(sym.AND); }
    "||" { return symbol(sym.OR); }
    "^" { return symbol(sym.XOR); }
    "!" { return symbol(sym.NOT); }

    "}"
    {
        if(prevState_EmbeddedUfe != UFEX_PROPERTIES){//UFEX_PROPERTIES es el unico caso en el que no regresa a algun estado de PLAIN_TEXT
            stringBuilder.setLength(0);
        }
        yybegin(prevState_EmbeddedUfe);
        return symbol(sym.C_CURLY);
    }

    {WhiteSpace} { /* igonore */ }
    /* comments */
    {Comment} { /* ignore */ }
}
/* error fallback */
[^] { 
    MyError error = new MyError(MyErrorType.LEXIC, Phase.UFE, path, yyline + 1, yycolumn + 1, "Illegal character <"+ yytext()+">");
    MySystem.Console.println(error);
    //Pasar a consola
    //MyGui.MainJFrame.logln(error);
}
