/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeEnvironment;

import MyObjects.MyError;
import ParserCSS.CssHelper;
import ParserCSS.UfeComponentStylizer;
import UfeInterfaces.AstNode;
import UfeInterfaces.UfeVisitor;
import UfeNodes.CssImport;
import UfeNodes.Def;
import UfeNodes.Expr;
import UfeNodes.Render;
import UfeNodes.Root;
import UfeNodes.Stmt;
import UfeNodes.Stmt.Block;
import UfeNodes.UfeImport;
import UfeNodes.Ufex.*;
import UfeNodes._Else;
import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Matcher;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * Contiene las variables de globales (almacendas en scope) y todos los metodos necesarios para recorrer cualquier nodo ufe dadas las varialble globales mencionadas anteriormente
 * @author Alvarez
 */
public final class Runner implements UfeVisitor{

    public Scope scope;//Chapuz alto para conseguir las variables para hacer la tabla de simbolos en el MainFrame
    private ParserCSS.Runtime cssCompiler;
    public final String path;
    public final String parentPath;
    public CssHelper cssHelper;//Chapuz alto para conseguir las variables para hacer la tabla de simbolos en el MainFrame
    public LinkedList<ImportedInstance> imports;//Chapuz alto para conseguir las variables para hacer la tabla de simbolos en el MainFrame
    private LinkedList<Def> definitions;//Para poder visitarlas desde adentro de otra definicio
    
    private ErrorMaker errorMaker;
    private BiExprHelper biExprHelper;
    
    public Runner(Root root) {
        scope = new Scope();
        path = root.path;
        errorMaker = new ErrorMaker(path);
        biExprHelper = new BiExprHelper();
        parentPath = initParentPath();
        cssCompiler = ParserCSS.Runtime.getInstance();
        
        this.visit(root);
    }
    
    /**
     * Retorna verdadero si el runner contiene un def con idComponente igual a id. No incluye los importados
     * @param id
     * @return 
     */
    public boolean containsCompDef(String id){
        for (Def def : this.definitions) {
            if (def.componentId.equals(id)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * SI this.scope NO ES GLOBAL ENTONCES TIRA EXCEPTION
     * @param id del componenete
     * @return el accept de el componente con el mismo nombre o un MyError no reportado
     */
    public Object getComponentById(String id){
        
        assert scope.isGlobal() : "No se puede ejecutar el metodo visitDef(String) cuando this.scope no es global";
        
        for(Def def : definitions){
            String defName = def.componentId.toLowerCase(Locale.ROOT);
            if (defName.equals(id)) {
                return def.accept(this);
            }
        }
        
        for (ImportedInstance _import : imports) {
            Object obj = _import.visitDef(id);
            if (obj != null) {
                return obj;
            }
        }
        
        return errorMaker.get("No exite ningun componente con el nombre: " + id);
    }
    
    @Override
    public Object visit(Root root) {
        //Variables locales
        for (Stmt stmt : root.stmts) {
            stmt.accept(this);
        }
        
        //importaciones css
        root.cssImport.accept(this);
        
        //importaciones ufe
        imports = new LinkedList<ImportedInstance>();
        for (UfeImport _import : root.ufeImports) {
            _import.accept(this);
        }
        
        //'Define' los def. no los ejecuta
        ArrayList<Def> tmpDefs = new ArrayList<>(root.defs);
        for (int i = 0; i < tmpDefs.size(); i++) {
            String defPivot = tmpDefs.get(i).componentId.toLowerCase(Locale.ROOT);
            for (int e = i + 1; e < tmpDefs.size(); e++) {
                String def = tmpDefs.get(e).componentId.toLowerCase(Locale.ROOT);
                if (defPivot.equals(def)) {
                    MyError error = errorMaker.get("Una o mas definiciones para: " + def + ". se utilizara la ultima en delclararse", root);
                    MySystem.Console.println(error);
                    tmpDefs.remove(i);
                    i = 0;
                    break;
                }
            }
        }
        definitions = new LinkedList<>(tmpDefs);
        
        return null;
    }
    
    @Override
    public Object visit(CssImport cssImport) {
        if (cssImport.path == null) {//Si tiene path null es porque no se hizo niguna sentencia import css en hll
            return null;
        }
        
        String src = getAbsolutePath(cssImport.path);
        CssHelper temp = cssCompiler.compile(src);
        
        if (temp == null) {
            MyError error = errorMaker.get(" Direccion: <" + src + "> no es valida", cssImport);
        }
        
        cssHelper = temp;
        return null;
    }

    @Override
    public Object visit(UfeImport ufeImport) {
        
        String src = getAbsolutePath(ufeImport.path);
        
        for(ImportedInstance _import : imports){
            if (_import.path.equals(src)) {
                MyError error = _import.addId(ufeImport.id);
                if (error != null) {
                    error = errorMaker.setTo(error, ufeImport);
                    MySystem.Console.println(error);
                    return null;
                }
            }
        }
        ImportedInstance newImport = ImportedInstance.create(src);
        if (newImport == null) {
            MyError error = errorMaker.get("No se pudo hacer la importacion a: " + ufeImport.id + " from: " + src, ufeImport);
            MySystem.Console.println(error);
            return null;
        }
        MyError error = newImport.addId(ufeImport.id);
        if (error != null) {
            error = errorMaker.setTo(error, ufeImport);
            MySystem.Console.println(error);
            return null;
        }
        imports.add(newImport);
        return null;
    }

    //PENDIENTE: QUITAR
    @Override
    public Object visit(Render render) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Object visit(Def def) {
        pushScope();
        Object obj;
        for(Stmt stmt : def.stmts){
            obj = stmt.accept(this);
            if (obj instanceof CompListDel) {
                popScope();
                return (CompListDel) obj;
            }
        }
        MyError error = errorMaker.get("Definicion no tiene retorno", def);//Pendiente: agregar a consola
        MySystem.Console.println(error);
        popScope();
        return error;
    }
    
    @Override
    public Object visit(Stmt.Decl decl) {
        for (Stmt.Decl.Item item : decl.items) {
            Object value = item.expr.accept(this);
            MyError error =scope.add(item.id, value);
            if (error != null) {
                errorMaker.setTo(error, decl);
                MySystem.Console.println(error);
                //Pendiente reportar otro error con linea en consola
            }
        }
        return null;
    }

    @Override
    public Object visit(Stmt.Assign assign) {
        Object value = assign.expr.accept(this);
        MyError error =scope.set(assign.id, value);
        if (error != null) {
            errorMaker.setTo(error, assign);
            //Pendiente reportar error con linea en consola
            MySystem.Console.println(error);
        }
        return null;
    }

    @Override
    public Object visit(Stmt.ArrayDecl arrayDecl) {
        //Se puede declarar un array con una lista de valores o con una tamanno
        //Si lista es null, se declaro con tamanno, si lista no es null se declaro con lista. Se podria optimizar verificando si se puede agregar la llave antes de hacer el recorrido de la lista de expr
        if (arrayDecl.values != null) {
            LinkedList<Object> vals = new LinkedList<>();
            Object val;
            for(Expr e : arrayDecl.values){
                val = e.accept(this);
                if (val instanceof MyError) {
                    MyError error = errorMaker.get("Un array no puede contener un valor tipo: " + val.getClass().getName(), arrayDecl);
                    MySystem.Console.println(error);//Pendiente: pasar a consola
                    return null;
                }
                vals.add(val);
            }
            scope.add(arrayDecl.id, vals.toArray());
        }
        else{
            Object index = arrayDecl.size.accept(this);
            if (!(index instanceof Integer)) {
                MyError error = errorMaker.get("Se requiere un Integer para crear un Array. " + index.getClass().getName() + " No es valido", arrayDecl);
                MySystem.Console.println(error);//Pendiente: reportar otro error y pasar a consola
                return null;
            }
            Integer intIndex= (Integer) index;
            if (intIndex < 1) {
                MyError error = errorMaker.get("Valor de entero no valido: <" + intIndex.intValue() + ">", arrayDecl);
                MySystem.Console.println(error);//Pendiente: reportar otro error y pasar a consola
                return null;
            }
            scope.add(arrayDecl.id, new Object[intIndex]);
        }
        return null;
    }

    @Override
    public Object visit(Stmt.ArrayAssign arrayAssign) {
        Object index = arrayAssign.index.accept(this);
        if (!(index instanceof Integer)) {
            MyError error = errorMaker.get("Indice no valido, se esperaba un tipo Integer, se obtuvo: " + index.getClass().getName(), arrayAssign);
            MySystem.Console.println(error);//Pendiente pasar a consola
            return null;
        }
        Object value = arrayAssign.value.accept(this);
        if (value instanceof MyError) {
            MyError error = errorMaker.get("no se le puede asignar un valor tipo MyError a una posicion de un array", arrayAssign);
            MySystem.Console.println(error);//Pendiente pasar a consola
            return null;
        }
        Object obj =scope.get(arrayAssign.id);
        if (obj instanceof MyError) {
            MyError error = errorMaker.setTo((MyError)obj, arrayAssign);
            MySystem.Console.println(error);//Pendiente pasar a consola
            return null;
        }
        if (!(obj instanceof Object[])) {
            MyError error = errorMaker.get(arrayAssign.id + " no es un array", arrayAssign);
            MySystem.Console.println(error);//pendiente pasar a consola
            return null;
        }
        Object[] array = (Object[]) obj;
        array[(Integer)index] = value;
        return null;
    }

    @Override
    public Object visit(Stmt.Print print) {
        Object obj = print.expr.accept(this);
        MySystem.Console.println(String.valueOf(obj));//Pendiente pasar a consola real
        return null;
    }
    
    @Override
    public Object visit(Block block) {
        pushScope();
        Object obj;
        for (Stmt stmt : block.stmts) {
            obj = stmt.accept(this);
            if (obj instanceof CompListDel) {
                popScope();
                return obj;
            }
        }
        popScope();
        return null;
    }
    
    @Override
    public Object visit(Stmt._If _if) {
        Object obj = _if.cond.accept(this);
        if (obj == null) {
            MyError error = errorMaker.get("null no es un boolean valido. Se tomo como falso", _if);
            MySystem.Console.println(error);
            return null;
        }
        if (!(obj instanceof Boolean)) {
            MyError error = errorMaker.get(obj.getClass().getName() + " no es un boolean valido. Se tomo como falso", _if);
            MySystem.Console.println(error);
            return null;
        }
        if ((Boolean) obj) {
            return _if.block.accept(this);
        }
        return null;
    }

    //Si la condicion tiene algun error se toma como falsa
    @Override
    public Object visit(Stmt._IfElse _ifElse) {
        Object obj = _ifElse.cond.accept(this);
        if (obj == null) {
            MyError error = errorMaker.get("null no es un boolean valido. Se tomo como falso", _ifElse);
            MySystem.Console.println(error);
            return _ifElse._else.accept(this);
        }
        if (!(obj instanceof Boolean)) {
            MyError error = errorMaker.get(obj.getClass().getName() + " no es un boolean valido. Se tomo como falso", _ifElse);
            MySystem.Console.println(error);
            return _ifElse._else.accept(this);
        }
        if ((Boolean) obj) {
            return _ifElse.block.accept(this);
        }
        else{
            return _ifElse._else.accept(this);
        }
    }

    @Override
    public Object visit(_Else _else) {
        return _else.stmt.accept(this);
    }

    @Override
    public Object visit(Stmt.Repeat repeat) {
        Object limit = repeat.limit.accept(this);
        if (!(limit instanceof Integer)) {
            MyError error = errorMaker.get("La expresion de Repetir no es un Integer valido", repeat);
            MySystem.Console.println(error);
            return null;
        }
        int flag = 0;
        Object obj;
        while((Integer) limit > flag){
            obj = repeat.block.accept(this);//Ejecuta el entorno
            
            if (obj instanceof CompListDel) {
                return obj;
            }
            
            limit = repeat.limit.accept(this);
            if (!(limit instanceof Integer)) {
                MyError error = errorMaker.get("Error en la repticion: " + (flag + 1) + " La expresion de Repetir no es un Integer valido", repeat);
                MySystem.Console.println(error);//Pendiente: pasar a consola
                return null;
            }
            flag++;
        }
        return null;
    }

    @Override
    public Object visit(Stmt._While _while) {
        Object cond = _while.cond.accept(this);
        if (!(cond instanceof Boolean)) {
            MyError error = errorMaker.get("La expresion de Mientras no es un Integer valido", _while);
            MySystem.Console.println(error);//Pendiente: pasar a consola
            return null;
        }
        Object obj;
        while((Boolean) cond){
            obj = _while.block.accept(this);//Ejecuta el entorno
            
            if (obj instanceof CompListDel) {
                return obj;
            }
            
            cond = _while.cond.accept(this);
            if (!(cond instanceof Boolean)) {
                MyError error = errorMaker.get(" La expresion de Mientras no es un Integer valido", _while);
                MySystem.Console.println(error);//Pendiente: pasar a consola
                return null;
            }
        }
        return null;
    }
    
    @Override
    public Object visit(Expr.AtomicExpr expr) {
        return expr.value;
    }
    
    @Override
    public Object visit(Expr.IdExpr expr) {
        Object result = scope.get(expr.id);
        if (result instanceof MyError) {
            MyError error = errorMaker.setTo((MyError) result, expr);
            MySystem.Console.println(error);
            return error;
        }
        return result;
    }
    
    @Override
    public Object visit(Expr.ArrayIndexExpr expr) {
        Object index = expr.index.accept(this);
        if (!(index instanceof Integer)) {
            MyError error = errorMaker.get("Indice no valido, se esperaba un tipo Integer, se obtuvo: " + index.getClass().getName(), expr);
            MySystem.Console.println(error);//Pendiente reportar error
            return error;
        }
        Object obj =scope.get(expr.id);
        if (obj instanceof MyError) {
            MyError error = errorMaker.setTo(((MyError)obj), expr);
            MySystem.Console.println(error);
            return error;
        }
        if (!(obj instanceof Object[])) {
            MyError error = errorMaker.get(expr.id + " no es un array", expr);
            MySystem.Console.println(error);
            return error;
        }
        Object[] array = (Object[]) obj;
        if ((Integer)index >= array.length || (Integer)index < 0) {
            MyError error = errorMaker.get("Indice: " + (Integer)index + " fuera de rango para array: " + expr.id, expr);
            MySystem.Console.println(error);
            return error;
        }
        return array[(Integer)index];
    }

    @Override
    public Object visit(Expr.ULogicExpr expr) {
        Object e1 = expr.expr1.accept(this);
        if (e1 == null) {
            MyError error = errorMaker.get("No se puede aplicar la operacion unaria: <" + expr.op.toString() + "> a null" , expr);
            MySystem.Console.println(error);
            return error;
        }
        if (e1 instanceof MyError) {
            return e1;
        }
        //Se hace la operacion
        switch (expr.op) {
            case NOT:
            {
                if (e1 instanceof Boolean) {
                    return !((Boolean) e1);
                }
                else{
                    return errorMaker.get("No se puede aplicar la operacion unaria: <" + expr.op.toString() + "> a: " + e1.getClass().getName(), expr);
                }
            }
            default:
                throw new AssertionError("Operador unario no valido: <" + expr.op);
        }
    }

    @Override
    public Object visit(Expr.UArithExpr expr) {
        Object e1 = expr.expr1.accept(this);
        if (e1 == null) {
            MyError error = errorMaker.get("No se puede aplicar la operacion unaria: <" + expr.op.toString() + "> a null" , expr);
            MySystem.Console.println(error);
            return error;
        }
        if (e1 instanceof MyError) {
            return e1;
        }
        //Se hace la operacion
        switch (expr.op) {
            case UMINUS:
            {
                if (e1 instanceof Integer) {
                    return (Integer) e1 * -1;
                }
                else if (e1 instanceof Character) {
                    return (Character) e1 * -1;
                }
                else if (e1 instanceof Double) {
                    return (Double) e1 * -1;
                }
                else{
                    return errorMaker.get("No se puede aplicar la operacion unaria: <" + expr.op.toString() + "> a: " + e1.getClass().getName(), expr);
                }
            }
            default:
                throw new AssertionError("Operador unario no valido: <" + expr.op);
        }
    }

    @Override
    public Object visit(Expr.BiCompExpr expr) {
        Object e1 = expr.expr1.accept(this);
        Object e2 = expr.expr2.accept(this);
        if (e1 == null) {
            MyError error = errorMaker.get("No se puede aplicar la operacion binaria: <" + expr.op.toString() + "> a null (e1)" , expr);
            MySystem.Console.println(error);//pendiente pasar a consola
            return error;
        }
        if (e2 == null) {
            MyError error = errorMaker.get("No se puede aplicar la operacion binaria: <" + expr.op.toString() + "> a null (e2)" , expr);
            MySystem.Console.println(error);//pendiente pasar a consola
            return error;
        }
        if (e1 instanceof MyError) {
            return e1;
        }
        if (e2 instanceof MyError) {
            return e2;
        }
        
        Object obj = biExprHelper.compare(e1, e2, expr.op);
        if (obj instanceof MyError) {
            return errorMaker.setTo((MyError) obj, expr);
        }
        return obj;
    }

    @Override
    public Object visit(Expr.BiLogicExpr expr) {
        Object e1 = expr.expr1.accept(this);
        Object e2 = expr.expr2.accept(this);
        if (e1 == null) {
            MyError error = errorMaker.get("No se puede aplicar la operacion binaria: <" + expr.op.toString() + "> a null (e1)" , expr);
            MySystem.Console.println(error);//pendiente pasar a consola
            return error;
        }
        if (e2 == null) {
            MyError error = errorMaker.get("No se puede aplicar la operacion binaria: <" + expr.op.toString() + "> a null (e2)" , expr);
            MySystem.Console.println(error);//pendiente pasar a consola
            return error;
        }
        if (e1 instanceof MyError) {
            return e1;
        }
        if (e2 instanceof MyError) {
            return e2;
        }
        
        Object obj = biExprHelper.logic(e1, e2, expr.op);
        if (obj instanceof MyError) {
            return errorMaker.setTo((MyError) obj, expr);
        }
        return obj;
    }

    @Override
    public Object visit(Expr.BiArithExpr expr) {
        Object e1 = expr.expr1.accept(this);
        Object e2 = expr.expr2.accept(this);
        if (e1 == null) {
            MyError error = errorMaker.get("No se puede aplicar la operacion binaria: <" + expr.op.toString() + "> a null (e1)" , expr);
            MySystem.Console.println(error);//pendiente pasar a consola
            return error;
        }
        if (e2 == null) {
            MyError error = errorMaker.get("No se puede aplicar la operacion binaria: <" + expr.op.toString() + "> a null (e2)" , expr);
            MySystem.Console.println(error);//pendiente pasar a consola
            return error;
        }
        if (e1 instanceof MyError) {
            return e1;
        }
        if (e2 instanceof MyError) {
            return e2;
        }
        
        Object obj = biExprHelper.arithmetic(e1, e2, expr.op);
        if (obj instanceof MyError) {
            return errorMaker.setTo((MyError) obj, expr);
        }
        return obj;
    }

    @Override
    public Object visit(Expr.BiConcatExpr expr) {
        Object e1 = expr.expr1.accept(this);
        Object e2 = expr.expr2.accept(this);
        if (e1 == null) {
            MyError error = errorMaker.get("No se puede aplicar la operacion binaria: <" + expr.op.toString() + "> a null (e1)" , expr);
            MySystem.Console.println(error);//pendiente pasar a consola
            return error;
        }
        if (e2 == null) {
            MyError error = errorMaker.get("No se puede aplicar la operacion binaria: <" + expr.op.toString() + "> a null (e2)" , expr);
            MySystem.Console.println(error);//pendiente pasar a consola
            return error;
        }
        if (e1 instanceof MyError) {
            return e1;
        }
        if (e2 instanceof MyError) {
            return e2;
        }
        
        Object obj = biExprHelper.concat(e1, e2, expr.op);
        if (obj instanceof MyError) {
            return errorMaker.setTo((MyError) obj, expr);
        }
        return obj;
    }
    
    @Override
    public Object visit(Stmt.Return ret){
        Object obj;
        LinkedList<JComponent> jComponentList = new LinkedList<>();
        int i = 0;
        for (Comp comp : ret.components) {
            i++;
            obj = comp.accept(this);
            if (obj == null) {
                MyError error = errorMaker.get("El elemento numero: " + i + ". No es un componente valido. Es null ", comp);
                MySystem.Console.println(error);
                continue;
            }
            if (obj instanceof JComponent) {
                jComponentList.add((JComponent) obj);
                continue;
            }
            if (obj instanceof CompListDel) {
                jComponentList.addAll(((CompListDel) obj).components);
                continue;
            }
            
            MyError error = errorMaker.get("El elemento numero: " + i + ". No es un componente valido. tipo: " + obj.getClass().getSimpleName(), comp);
            MySystem.Console.println(error);
        }
        return new CompListDel(jComponentList);
    }
    
    /**
     * Restorna si el tipo de obj es adecuado para un propiedad k
     * @param k
     * @param obj
     * @return 
     */
    private boolean isValidProp(PropType k, Object obj) {
        switch (k) {
            case ID:
            case CLASSNAME:
            case SRC:
                return obj instanceof String;
            case X:
            case Y:
            case WIDTH:
            case HEIGHT:
            case BORDER:
            case MIN:
            case MAX:
                return obj instanceof Integer;
            case COLOR:
                return obj instanceof String || obj instanceof Color || obj instanceof Integer;
            case ONCLICK:
                return obj != null && !(obj instanceof MyError);
            default:
                throw new AssertionError("isValidProp: propType no existe");
        }
    }
    
    /**
     * Crea un nuevo componente default con el estilo css y propiedades definidas en comp.properties
     * Verifica que cada prop en comp.properties sea valida, de no ser asi, reporta el MyError y se ignora.
     * Le aplica el estilo css, si ocurre error en esta operacion reporta el error y no se aplica el estilo css al componente
     * @param type No puede ser CUSTOM. de ser asi tira una exception no reportada
     * @param comp
     * @return 
     */
    private JComponent getDefaultComponent(CompType type, Comp.Default comp){
        HashMap<PropType, Object> propRuntimeValues = new HashMap<>();//Propiedades despues de recorre cada nodo prop value
        comp.properties.forEach((k, v) -> {
            if (!k.isDefault()) {//Se salta todas las llaves no default
                return;
            }
            Object obj = v.accept(this);
            if (obj instanceof MyError) {
                return;
            }
            if (!isValidProp(k, obj)) {
                MyError error = errorMaker.get("A la propiedad: " + k.toString() + " No se le puede asignar un valor de tipo: " + obj.getClass().getName(), v);
                MySystem.Console.println(error);//Pendiente: pasar a consola
                return;
            }
            propRuntimeValues.put(k, obj);
        });
        
        JComponent jComp = CompFactory.getComp(type, propRuntimeValues);
        
        //Setea las propiedades definidas en css
        String className = (String) propRuntimeValues.get(PropType.CLASSNAME);
        if (className != null) {
            if (cssHelper != null) {
                Object obj = cssHelper.get(className);
                if (obj instanceof MyError) {
                    MyError error = errorMaker.setTo((MyError) obj, comp);
                    MySystem.Console.println(error);
                    return jComp;
                }
                ((UfeComponentStylizer) obj).stylize(jComp);
            }else{
                MyError error = errorMaker.get("Clase css: " + className + " para el componente: " + jComp.getName() + " no valida. CssHelper es null. ", comp);
                MySystem.Console.println(error);
            }
        }
        
        
        return jComp;
    }
    
    //Ufex
    @Override
    public Object visit(Comp.Default.Panel panel) {
        JPanel jPanel = (JPanel) getDefaultComponent(CompType.PANEL, panel);
        panel.components.forEach((Comp comp) -> { 
            Object obj = comp.accept(this);
            if (obj == null) {
                MyError error = errorMaker.get("No sepuede agregar null a panel: " + jPanel.getName() , comp);
                MySystem.Console.println(error);//Pendiente: pasar a consola
                return;
            }
            
            if (obj instanceof JComponent) {
                jPanel.add((JComponent) obj);
            }
            else if(obj instanceof CompListDel){
                
                CompListDel objLang = (CompListDel) obj;
                objLang.components.forEach((val) -> {
                    jPanel.add(val);
                });
                
            }
            else{
                MyError error = errorMaker.get("No sepuede agregar el tipo: [" + obj.getClass().getSimpleName() + "] a panel: " + jPanel.getName(), comp);
                MySystem.Console.println(error);//Pendiente: pasar a consola
            }
        });
        
        return jPanel;
    }

    @Override
    public Object visit(Comp.Default.Text text) {
        JLabel jLabel = (JLabel) getDefaultComponent(CompType.TEXT, text);
        
        String content = (String) text.contentGroup.accept(this);
        if (content == null) {
            content = "";
        }
        
        String htmlLblContent;
        
        switch (jLabel.getHorizontalAlignment()) {
            case javax.swing.SwingConstants.LEFT:
                htmlLblContent = "<html><p align=\"left\">" + content + "</p></html>";
                break;
            case javax.swing.SwingConstants.CENTER:
                htmlLblContent = "<html><p align=\"center\">" + content + "</p></html>";
                break;
            case javax.swing.SwingConstants.RIGHT:
                htmlLblContent = "<html><p align=\"right\">" + content + "</p></html>";
                break;
            default:
                htmlLblContent = "<html><p>" + content + "</p></html>";//alineacion default
        }
        
        jLabel.setText(htmlLblContent);
        
        return jLabel;
    }

    @Override
    public Object visit(Comp.Default.TextField textField) {
        JTextField jTextField = (JTextField) getDefaultComponent(CompType.TEXT_FIELD, textField);
        
        String content = (String) textField.contentGroup.accept(this);
        if (content == null) {
            content = "";
        }
        
        jTextField.setText(content);
        
        return jTextField;
    }

    @Override
    public Object visit(Comp.Default.Button button) {
        JButton jButton = (JButton) getDefaultComponent(CompType.BUTTON, button);
        
        String content = (String) button.contentGroup.accept(this);
        if (content == null) {
            content = "";
        }
        
        jButton.setText(content);
        
        PropValue propValue = button.properties.get(PropType.ONCLICK);
        if (propValue == null) {
            return jButton;
        }
        Object obj = propValue.accept(this);
        if (obj instanceof MyError) {
            MyError error = errorMaker.get("No se puede asignar un mensaje: " + obj.getClass().getSimpleName() + " al Button: " + jButton.getName(), button);
            MySystem.Console.println(error);//Pasar a consola
            return jButton;
        }
        
        jButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JOptionPane.showMessageDialog(null, obj.toString());
            }
        });
        
        return jButton;
    }

    @Override
    public Object visit(Comp.Default.List list) {
        JComboBox jComboBox = (JComboBox) getDefaultComponent(CompType.LIST, list);
        
        LinkedList<String> items = new LinkedList();
        String itemContent;
        for (CntGroup contentGroup : list.items) {
            itemContent = (String) contentGroup.accept(this);
            if (itemContent == null) {
                itemContent = "";
            }
            items.add(itemContent);
        }
        String[] stringItems = new String[items.size()];
        int i = 0;
        for (String item : items) {
            stringItems[i] = item;
            i++;
        }
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(stringItems);
        jComboBox.setModel(model);
        
        String defContent = (String) list._default.accept(this);
        if (defContent != null) {
            //Verifica que exita el elemento
            if (!defContent.matches("[0-9]+")) {//Revisa que sea un integer valido
                MyError error = errorMaker.get("El default: [" + defContent + "] no es un integer valido. Componente List: " + jComboBox.getName(), list);//Pasar a consola
                MySystem.Console.println(error);
                return jComboBox;
            }
            int index = Integer.valueOf(defContent);
            if (index >= items.size() || index < 0) {
                MyError error = errorMaker.get("El default: [" + index + "] esta fuera de rango. Componente List: " + jComboBox.getName(), list);//Pasar a consola
                System.out.println(error);
                return jComboBox;
            }
            jComboBox.setSelectedIndex(index);
        }
        
        return jComboBox;
    }

    private final int DEFAULT_MIN = -100;
    private final int DEFAULT_MAX = 100;
    
    @Override
    public Object visit(Comp.Default.Spinner spinner) {
        JSpinner jSpinner = (JSpinner) getDefaultComponent(CompType.SPINNER, spinner);
        
        int min = DEFAULT_MIN;
        int max = DEFAULT_MAX;
        int val = min;
        
        PropValue propValue = spinner.properties.get(PropType.MIN);
        if (propValue != null) {
            Object obj = propValue.accept(this);
            if (obj instanceof Integer) {
                min = (Integer) obj;
            }
            else{
                MyError error = errorMaker.get("No se puede setear un valor tipo " + obj.getClass().getSimpleName() + " al minimo del spinner: " + jSpinner.getName(), propValue);
                MySystem.Console.println(error);//Pendiente pasar a consola
            }
        }
        
        propValue = spinner.properties.get(PropType.MAX);
        if (propValue != null) {
            Object obj = propValue.accept(this);
            if (obj instanceof Integer) {
                max = (Integer) obj;
            }
            else{
                MyError error = errorMaker.get("No se puede setear un valor tipo " + obj.getClass().getSimpleName() + " al maximo del spinner: " + jSpinner.getName(), propValue);
                MySystem.Console.println(error);//Pendiente pasar a consola
            }
        }
        
        Object obj = spinner._default.accept(this);
        if (obj instanceof Integer) {
            val = (Integer) obj;
        }
        else if(obj instanceof String){
            String st = (String) obj;
            if (st.matches("[0-9]+")) {
                val = Integer.valueOf(st);
            }
            else{
                MyError error = errorMaker.get("No se puede setear la cadena: " + st + " al default de: " + jSpinner.getName(), spinner);
                MySystem.Console.println(error);//Pendiente pasar a consola
            }
        }
        else if(obj != null){//Si obj es null es porque venia vacio el contenido => no se reporta error y se usa el valor default. Chapuz medio
            MyError error = errorMaker.get("No se puede setear un valor tipo " + obj.getClass().getSimpleName() + " al default del spinner: " + jSpinner.getName(), spinner);
            MySystem.Console.println(error);//Pendiente pasar a consola
        }
        
        if (!(min <= val && val <= max)) {
            MyError error = errorMaker.get("No se puede crear un spinner " + jSpinner.getName() + " con los valores. " + "max: " + max + " min: " + min + " val: " + val + 
                    " minimum <= value <= maximum es falso. Se utilizaron los valores default", spinner);
            MySystem.Console.println(error);//Pendiente pasar a consola
            min = DEFAULT_MIN;
            max = DEFAULT_MAX;
            val = min;
        }
        
        jSpinner.setModel(new SpinnerNumberModel(val, min, max, 1));
        
        return jSpinner;
    }

    @Override
    public Object visit(Comp.Default.Image image) {
        
        JLabel jLabel = (JLabel) getDefaultComponent(CompType.IMAGE , image);
        
        String src = "";
        
        PropValue propValue = image.properties.get(PropType.SRC);
        if (propValue != null) {
            Object obj = propValue.accept(this);
            if (obj instanceof String) {
                src = (String) obj;
            }
            else{
                MyError error = errorMaker.get("No se puede setear un valor tipo " + obj.getClass().getSimpleName() + " al scr de la Image: " + jLabel.getName() + ". se mostrara la imagen default", propValue);
                MySystem.Console.println(error);//Pendiente pasar a consola
                jLabel.setText(error.toString());
                return jLabel;
            }
        }
        
        src = getAbsolutePath(src);
        
        Image newImage;
        try {
            newImage = ImageIO.read(new File(src));
        } catch (IOException ex) {
            MyError error = errorMaker.get("Imagen no valida para: " + jLabel.getName() + " direccion no valida: <" + src + ">", image);
            MySystem.Console.println(error);//Pasar a consola
            jLabel.setText("<html>" + error.toString() + "</html>");
            return jLabel;
        }
        newImage = newImage.getScaledInstance(jLabel.getWidth(), jLabel.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(newImage);
        jLabel.setIcon(imageIcon);
        
        return jLabel;
    }

    @Override
    public Object visit(Comp.Custom custom) {
        //Buscar entre las definiciones de este runner
        String id = custom.id;
        for(Def def : this.definitions){
            if (id.equals(def.componentId)) {
                Scope current = this.scope;
                gotoGlobalScope();
                Object tmp = def.accept(this);
                this.scope = current;
                return tmp;
            }
        }
        for(ImportedInstance _import : this.imports){
            Object tmp = _import.visitDef(id);
            if (tmp != null) {
                return tmp;
            }
        }
        return errorMaker.get("No exite definicion para el componente: " + id, custom);
    }

    //Retorna null si esta vacio o todo Cnt en content group retorno MyError o null
    @Override
    public Object visit(CntGroup cntGroup) {
        
        StringBuilder sb = new StringBuilder();
        boolean b = false;//Chapuz minimo para determina si se le hizo append a sb al menos 1 vez
        for(Cnt content : cntGroup.contents) {
            Object obj = content.accept(this);
            if (obj instanceof MyError) {
                continue;
            }
            if (obj == null) {
                continue;
            }
            b = true;
            sb.append(obj.toString());
        }
        
        if (!b) {
            return null;
        }
        
        return sb.toString();
    }

    @Override
    public Object visit(Cnt.PlainTxt plainText) {
        return plainText.txt;
    }

    @Override
    public Object visit(Cnt.EmbUfe embUfe) {
        return embUfe.expr.accept(this);
    }

    @Override
    public Object visit(PropValue.IdProp idProp) {
        return idProp.id;
    }

    @Override
    public Object visit(PropValue.AtomicProp atomicProp) {
        return atomicProp.value;
    }

    @Override
    public Object visit(PropValue.EmbProp embProp) {
        return embProp.expr.accept(this);
    }
    
    /**
     * Hace pop al scope hasta llegar al global GUARDAR UNA REFERENCIA AL SCOPE ACTUAL ANTES DE USAR. SI NO SE PIERDE INFORMACION
     */
    private void gotoGlobalScope(){
        while(!scope.isGlobal()){
            popScope();
        }
    }
    
    public void pushScope(){
        scope = new Scope(scope);
    }
    
    public void popScope(){
        scope = scope.getOuterScope();
    }

    private String initParentPath() {
        java.io.File f = new File(path);
        return f.getParent();
    }

    private String getAbsolutePath(String src) {
        src = src.replaceAll("[/|\\\\]", Matcher.quoteReplacement(File.separator));
        if (src.charAt(0) == '.') { 
            src = parentPath + src.substring(1);
        }
        return src;
    }
    
    private class BiExprHelper{
        /**
         * RETORNA BOOLEAN RESULTANTE DE LA COMPARACION, SI LOS TIPOS NO SON VALIDOS RETORNA UN ERROR CON LOS DETALLES EN SU .msg
         * @param obj1
         * @param obj2
         * @param op
         * @return 
         */
        private Object compare(Object obj1, Object obj2, Expr.BiCompOp op){
            //Chapuz medio alto
            switch (op) {
                case LESS:
                    if (obj1 instanceof Integer) {
                        if (obj2 instanceof Integer) {
                            return (Integer) obj1 < (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Integer) obj1 < (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Integer) obj1 < (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Character) {
                        if (obj2 instanceof Integer) {
                            return (Character) obj1 < (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Character) obj1 < (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Character) obj1 < (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Double) {
                        if (obj2 instanceof Integer) {
                            return (Double) obj1 < (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Double) obj1 < (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Double) obj1 < (Double) obj2;
                        }
                    }
                    break;
                case GREATER:
                    if (obj1 instanceof Integer) {
                        if (obj2 instanceof Integer) {
                            return (Integer) obj1 > (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Integer) obj1 > (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Integer) obj1 > (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Character) {
                        if (obj2 instanceof Integer) {
                            return (Character) obj1 > (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Character) obj1 > (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Character) obj1 > (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Double) {
                        if (obj2 instanceof Integer) {
                            return (Double) obj1 > (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Double) obj1 > (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Double) obj1 > (Double) obj2;
                        }
                    }
                    break;
                case LESS_EQ:
                    if (obj1 instanceof Integer) {
                        if (obj2 instanceof Integer) {
                            return (Integer) obj1 <= (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Integer) obj1 <= (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Integer) obj1 <= (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Character) {
                        if (obj2 instanceof Integer) {
                            return (Character) obj1 <= (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Character) obj1 <= (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Character) obj1 <= (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Double) {
                        if (obj2 instanceof Integer) {
                            return (Double) obj1 <= (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Double) obj1 <= (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Double) obj1 <= (Double) obj2;
                        }
                    }
                    break;
                case GREATER_EQ:
                    if (obj1 instanceof Integer) {
                        if (obj2 instanceof Integer) {
                            return (Integer) obj1 >= (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Integer) obj1 >= (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Integer) obj1 >= (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Character) {
                        if (obj2 instanceof Integer) {
                            return (Character) obj1 >= (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Character) obj1 >= (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Character) obj1 >= (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Double) {
                        if (obj2 instanceof Integer) {
                            return (Double) obj1 >= (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Double) obj1 >= (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Double) obj1 >= (Double) obj2;
                        }
                    }
                    break;
                case EQ_EQ:
                    if (obj1 instanceof Integer) {
                        if (obj2 instanceof Integer) {
                            return ((Integer) obj1).intValue() == ((Integer) obj2).intValue();
                        }
                        else if (obj2 instanceof Character) {
                            return ((Integer) obj1).intValue() == ((Character) obj2).charValue();
                        }
                        else if (obj2 instanceof Double) {
                            return ((Integer) obj1).intValue() == ((Double) obj2).doubleValue();
                        }
                    }
                    else if (obj1 instanceof Character) {
                        if (obj2 instanceof Integer) {
                            return ((Character) obj1).charValue() == ((Integer) obj2).intValue();
                        }
                        else if (obj2 instanceof Character) {
                            return ((Character) obj1).charValue() == ((Character) obj2).charValue();
                        }
                        else if (obj2 instanceof Double) {
                            return ((Character) obj1).charValue() == ((Double) obj2).doubleValue();
                        }
                    }
                    else if (obj1 instanceof Double) {
                        if (obj2 instanceof Integer) {
                            return ((Double) obj1).doubleValue() == ((Integer) obj2).intValue();
                        }
                        else if (obj2 instanceof Character) {
                            return ((Double) obj1).doubleValue() == ((Character) obj2).charValue();
                        }
                        else if (obj2 instanceof Double) {
                            return ((Double) obj1).doubleValue() == ((Double) obj2).doubleValue();
                        }
                    }
                    else if (obj1 instanceof String) {
                        if (obj2 instanceof String) {
                            return obj1.equals(obj2);
                        }
                    }
                    break;
                case NOT_EQ:
                    if (obj1 instanceof Integer) {
                        if (obj2 instanceof Integer) {
                            return ((Integer) obj1).intValue() != ((Integer) obj2).intValue();
                        }
                        else if (obj2 instanceof Character) {
                            return ((Integer) obj1).intValue() != ((Character) obj2).charValue();
                        }
                        else if (obj2 instanceof Double) {
                            return ((Integer) obj1).intValue() != ((Double) obj2).doubleValue();
                        }
                    }
                    else if (obj1 instanceof Character) {
                        if (obj2 instanceof Integer) {
                            return ((Character) obj1).charValue() != ((Integer) obj2).intValue();
                        }
                        else if (obj2 instanceof Character) {
                            return ((Character) obj1).charValue() != ((Character) obj2).charValue();
                        }
                        else if (obj2 instanceof Double) {
                            return ((Character) obj1).charValue() != ((Double) obj2).doubleValue();
                        }
                    }
                    else if (obj1 instanceof Double) {
                        if (obj2 instanceof Integer) {
                            return ((Double) obj1).doubleValue() != ((Integer) obj2).intValue();
                        }
                        else if (obj2 instanceof Character) {
                            return ((Double) obj1).doubleValue() != ((Character) obj2).charValue();
                        }
                        else if (obj2 instanceof Double) {
                            return ((Double) obj1).doubleValue() != ((Double) obj2).doubleValue();
                        }
                    }
                    else if (obj1 instanceof String) {
                        if (obj2 instanceof String) {
                            return !obj1.equals(obj2);
                        }
                    }
                    break;
                default:
                    throw new AssertionError("Operador comparativo no valido");
            }
            return errorMaker.get("No se puede aplicar la operacion binaria comparativa: [" + op.toString() + "] a: " + obj1.getClass().getName() + " y " + obj2.getClass().getName());
        }
        
        private Object compare1(Object obj1, Object obj2, Expr.BiCompOp op){
            if (obj1 instanceof Character) {
                obj1 = new Integer(((Character)obj1));
            }
            if (obj2 instanceof Character) {
                obj2 = new Integer(((Character)obj2));
            }
            if (obj1 instanceof Number && obj2 instanceof Number) {
                Number n1 = (Number) obj1;
                Number n2 = (Number) obj2;
                switch (op) {
                    case LESS:
                        return n1.doubleValue() < n2.doubleValue();
                    case GREATER:
                        return n1.doubleValue() > n2.doubleValue();
                    case LESS_EQ:
                        return n1.doubleValue() <= n2.doubleValue();
                    case GREATER_EQ:
                        return n1.doubleValue() >= n2.doubleValue();
                    case EQ_EQ:
                        return n1.doubleValue() == n2.doubleValue();
                    case NOT_EQ:
                        return n1.doubleValue() != n2.doubleValue();
                    default:
                        throw new AssertionError("Operador comparativo no valido");
                }
            }
            else if (obj1 instanceof String && obj2 instanceof String) {
                switch (op) {
                    case LESS:
                    case GREATER:
                    case LESS_EQ:
                    case GREATER_EQ:
                        break;
                    case EQ_EQ:
                        return obj1.equals(obj2);
                    case NOT_EQ:
                        return !obj1.equals(obj2);
                    default:
                        throw new AssertionError("Operador comparativo no valido");
                }
            }
            return errorMaker.get("No se puede aplicar la operacion binaria comparativa: [" + op.toString() + "] a: " + obj1.getClass().getName() + " y " + obj2.getClass().getName());
        }

        private Object logic(Object obj1, Object obj2, Expr.BiLogicOp op) {
            if (!(obj1 instanceof Boolean)) {
                return errorMaker.get("No se puede hacer una operacion logica con tipo: " + obj1.getClass().getName());
            }
            if (!(obj2 instanceof Boolean)) {
                return errorMaker.get("No se puede hacer una operacion logica con tipo: " + obj2.getClass().getName());
            }
            Boolean b1 = (Boolean) obj1;
            Boolean b2 = (Boolean) obj2;
            switch (op) {
                case AND:
                    return b1 && b2;
                case OR:
                    return b1 || b2;
                case XOR:
                    return b1 ^ b2;
                default:
                    throw new AssertionError("Operador logico no valido");
            }
        }
        
        private Object arithmetic(Object obj1, Object obj2, Expr.BiArithOp op){
            if (!(obj1 instanceof Number || obj1 instanceof Character)) {
                return errorMaker.get("No se puede aplicar la operacion binaria aritmetica: [" + op.toString() + "] a: " + obj1.getClass().getName() + " y " + obj2.getClass().getName());
            }
            if (!(obj2 instanceof Number || obj2 instanceof Character)) {
                return errorMaker.get("No se puede aplicar la operacion binaria aritmetica: [" + op.toString() + "] a: " + obj1.getClass().getName() + " y " + obj2.getClass().getName());
            }
            switch (op) {
                case MINUS:
                    if (obj1 instanceof Integer) {
                        if (obj2 instanceof Integer) {
                            return (Integer) obj1 - (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Integer) obj1 - (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Integer) obj1 - (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Character) {
                        if (obj2 instanceof Integer) {
                            return (Character) obj1 - (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Character) obj1 - (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Character) obj1 - (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Double) {
                        if (obj2 instanceof Integer) {
                            return (Double) obj1 - (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Double) obj1 - (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Double) obj1 - (Double) obj2;
                        }
                    }
                    break;
                case MULT:
                    if (obj1 instanceof Integer) {
                        if (obj2 instanceof Integer) {
                            return (Integer) obj1 * (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Integer) obj1 * (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Integer) obj1 * (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Character) {
                        if (obj2 instanceof Integer) {
                            return (Character) obj1 * (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Character) obj1 * (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Character) obj1 * (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Double) {
                        if (obj2 instanceof Integer) {
                            return (Double) obj1 * (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Double) obj1 * (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Double) obj1 * (Double) obj2;
                        }
                    }
                    break;
                case DIV:
                    if (obj1 instanceof Integer) {
                        if (obj2 instanceof Integer) {
                            if ((Integer) obj2 == 0) {
                                return errorMaker.get("No se puede puede dividir con 0");
                            }
                            return (Integer) obj1 / (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            if (((Character) obj2).charValue() == 0) {
                                return errorMaker.get("No se puede puede dividir con 0");
                            }
                            return (Integer) obj1 / (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            if ((Double) obj2 == 0.0) {
                                return errorMaker.get("No se puede puede dividir con 0");
                            }
                            return (Integer) obj1 / (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Character) {
                        if (obj2 instanceof Integer) {
                            if ((Integer) obj2 == 0) {
                                return errorMaker.get("No se puede puede dividir con 0");
                            }
                            return (Character) obj1 / (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            if (((Character) obj2).charValue() == 0) {
                                return errorMaker.get("No se puede puede dividir con 0");
                            }
                            return (Character) obj1 / (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            if ((Double) obj2 == 0.0) {
                                return errorMaker.get("No se puede puede dividir con 0");
                            }
                            return (Character) obj1 / (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Double) {
                        if (obj2 instanceof Integer) {
                            if ((Integer) obj2 == 0) {
                                return errorMaker.get("No se puede puede dividir con 0");
                            }
                            return (Double) obj1 / (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            if (((Character) obj2).charValue() == 0) {
                                return errorMaker.get("No se puede puede dividir con 0");
                            }
                            return (Double) obj1 / (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            if ((Double) obj2 == 0.0) {
                                return errorMaker.get("No se puede puede dividir con 0");
                            }
                            return (Double) obj1 / (Double) obj2;
                        }
                    }
                    break;
                case POW:
                    if (obj1 instanceof Integer) {
                        if (obj2 instanceof Integer) {
                            return Math.pow((Integer) obj1, (Integer) obj2);
                        }
                        else if (obj2 instanceof Character) {
                            return Math.pow((Integer) obj1, (Character) obj2);
                        }
                        else if (obj2 instanceof Double) {
                            return Math.pow((Integer) obj1, (Double) obj2);
                        }
                    }
                    else if (obj1 instanceof Character) {
                        if (obj2 instanceof Integer) {
                            return Math.pow((Character) obj1, (Integer) obj2);
                        }
                        else if (obj2 instanceof Character) {
                            return Math.pow((Character) obj1, (Character) obj2);
                        }
                        else if (obj2 instanceof Double) {
                            return Math.pow((Character) obj1, (Double) obj2);
                        }
                    }
                    else if (obj1 instanceof Double) {
                        if (obj2 instanceof Integer) {
                            return Math.pow((Double) obj1, (Integer) obj2);
                        }
                        else if (obj2 instanceof Character) {
                            return Math.pow((Double) obj1, (Character) obj2);
                        }
                        else if (obj2 instanceof Double) {
                            return Math.pow((Double) obj1, (Double) obj2);
                        }
                    }
                    break;
                default:
                    throw new AssertionError("Operador aritmetico no valido");
            }
            return errorMaker.get("No se puede aplicar la operacion binaria aritmetica: [" + op.toString() + "] a: " + obj1.getClass().getName() + " y " + obj2.getClass().getName());
        }
        
        private Object concat(Object obj1, Object obj2, Expr.BiConcatOp op){
            if (obj1 instanceof String || obj2 instanceof String) {
                return obj1.toString() + obj2.toString();
            }
            switch (op) {
                case PLUS:
                    if (obj1 instanceof Integer) {
                        if (obj2 instanceof Integer) {
                            return (Integer) obj1 + (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Integer) obj1 + (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Integer) obj1 + (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Character) {
                        if (obj2 instanceof Integer) {
                            return (Character) obj1 + (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Character) obj1 + (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Character) obj1 + (Double) obj2;
                        }
                    }
                    else if (obj1 instanceof Double) {
                        if (obj2 instanceof Integer) {
                            return (Double) obj1 + (Integer) obj2;
                        }
                        else if (obj2 instanceof Character) {
                            return (Double) obj1 + (Character) obj2;
                        }
                        else if (obj2 instanceof Double) {
                            return (Double) obj1 + (Double) obj2;
                        }
                    }
                    break;
                default:
                    throw new AssertionError("Concat op no valido");
            }
            return errorMaker.get("No se puede aplicar la operacion binaria concatenacion: [" + op.toString() + "] a: " + obj1.getClass().getName() + " y " + obj2.getClass().getName());
        }
    }
    
    private class ErrorMaker{
        private String path = "Unknown";
        
        private ErrorMaker(String path){
            this.path = path;
        }
        
        private MyError get(String msg){
            return new MyError(msg);
        }
        
        private MyError get(String msg, AstNode node){
            return new MyError(MyError.MyErrorType.SEMANTIC, MyError.Phase.UFE_SYNTHETIZE, path, node.getLine(), node.getColumn(), msg);
        }
        
        /**
         * ESTE METODO CAMBIA LOS VALORES DEL PARAMETRO ERROR
         * Usa el AstNode para setear los atributos de un MyError. Setea todo menos el atributo msg de MyError
         * @param error
         * @param node 
         */
        private MyError setTo(MyError error, AstNode node){
            error.set(MyError.MyErrorType.SEMANTIC, MyError.Phase.UFE_SYNTHETIZE, path, node.getLine(), node.getColumn());
            return error;
        }
    }
}
