/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MySystem;

import static MyGUI.MainJFrame.myConsole;
import MyObjects.MyError;
import java.util.LinkedList;

/**
 *
 * @author Alvarez
 */
public class Console {
    
    public static LinkedList<MyError> MY_ERRORS = new LinkedList<MyError>();
    
    public static void print(String msg){
        myConsole.setText(myConsole.getText() + msg);
    }
    public static void println(String msg){
        myConsole.setText(myConsole.getText() + "\n" + msg);
    }
    
    public static void print(MyError error){
        MY_ERRORS.add(error);
        String msg = "\nError tipo: [" + error.type + "] en fase: [" + error.phase + "]. " + error.msg + ".\n"
                     + "    " + error.filePath + "\n"
                     + "    linea: " + error.line + ", columna: " + error.column;
        myConsole.setText(myConsole.getText() + msg);
    }
    
    public static void println(MyError error){
        MY_ERRORS.add(error);
        String msg = "\nError tipo: [" + error.type + "] en fase: [" + error.phase + "]. " + error.msg + ".\n"
                     + "    " + error.filePath + "\n"
                     + "    linea: " + error.line + ", columna: " + error.column;
        myConsole.setText(myConsole.getText() + msg);
    }
    
    public static String getHtmlReport(){
        String s =  "<html>\n" +
                    "	<head>\n" +
                    "		<title>Reporte de Errores</title>\n" +
                    "		<meta charset=\\\"UTF-8\\\">\n" +
                    "		<meta name=\\\"viewport\\\" content=\\\"width=device-width, initial-scale=1.0\\\">\n" +
                    "	</head>\n" +
                    "	<body>\n" +
                    "		<table style=\"width:100%\" border=\"1\">\n" +
                    "			<tr>\n" +
                    "				<th></th>\n" +
                    "				<th>Tipo</th> \n" +
                    "				<th>Fase</th> \n" +
                    "				<th>Linea</th>\n" +
                    "				<th>Columna</th>\n" +
                    "				<th>Dir</th>\n" +
                    "				<th>Mensaje</th>\n" +
                    "			</tr>";
        StringBuilder sb = new StringBuilder(s);
        int i = 1;
        for(MyError error : MY_ERRORS){
            sb.append("<tr>\n");
            sb.append("    <td>").append(i).append("\n");
            sb.append("    <td>").append(String.valueOf(error.type)).append("\n");
            sb.append("    <td>").append(String.valueOf(error.phase)).append("\n");
            sb.append("    <td>").append(error.line).append("\n");
            sb.append("    <td>").append(error.column).append("\n");
            sb.append("    <td>").append(String.valueOf(error.filePath)).append("\n");
            s = error.msg.replaceAll("\n", "<br>");
            sb.append("    <td>").append(s).append("\n");
            sb.append("</tr>\n");
            i++;
        }
        s       =   "		</table>\n" +
                    "	</body>\n" +
                    "</html>";
        sb.append(s);
        return sb.toString();
    }
}
