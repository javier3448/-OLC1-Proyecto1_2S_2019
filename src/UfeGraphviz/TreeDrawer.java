/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeGraphviz;

import UfeInterfaces.UfeVisitor;
import UfeNodes.CssImport;
import UfeNodes.Def;
import UfeNodes.Expr;
import UfeNodes.Render;
import UfeNodes.Root;
import UfeNodes.Stmt;
import UfeNodes.UfeImport;
import UfeNodes.Ufex;
import UfeNodes.Ufex.*;
import UfeNodes._Else;
import java.util.LinkedList;

/**
 * SOLO PARA DEBUGGING
 * @author Alvarez
 */
public class TreeDrawer implements UfeVisitor{

    public LinkedList<String> lines = new LinkedList();

    public TreeDrawer(Root root) {
        lines.add("digraph UfeAstTree{");
        lines.add("node[shape = record];");
        
        root.accept(this);
        
        lines.add("}");
        
        for(String s : lines){
            System.out.println(s);
        }
    }
    
    @Override
    public Object visit(Root root) {
        String name = root.dotId();
        lines.add(name + "[ label = \"Root\" ];");
        
        String stmtsName = "Stmts" + name;
        lines.add(stmtsName + "[label=" + "\"Stmts\" ];");
        String ufeImportsName = "ufeImports" + name;
        lines.add(ufeImportsName + "[label=" + "\"ufeImports\" ];");
        String cssImportName = "cssImport" + name;
        lines.add(cssImportName + "[label=" + "\"cssImport\" ];");
        String rendersName = "renders" + name;
        lines.add(rendersName + "[label=" + "\"renders\" ];");
        String defsName = "defs" + name;
        lines.add(defsName + "[label=" + "\"defs\" ];");
        
        lines.add(name + "->" + stmtsName);
        lines.add(name + "->" + ufeImportsName);
        lines.add(name + "->" + cssImportName);
        lines.add(name + "->" + rendersName);
        lines.add(name + "->" + defsName);
        
        for (Stmt stmt : root.stmts) {
            String stmtName = stmt.dotId();
            lines.add(stmtsName + "->" + stmtName + ";");
            stmt.accept(this);
        }
        
        for (UfeImport ufeImport : root.ufeImports) {
            String ufeImportName = ufeImport.dotId();
            lines.add(ufeImportsName + "->" + ufeImportName + ";");
            ufeImport.accept(this);
        }
        
        
        lines.add(cssImportName + "->" + root.cssImport.dotId() + ";");
        root.cssImport.accept(this);
        
        for (Render render : root.renders) {
            String renderName = render.dotId();
            lines.add(rendersName + "->" + renderName + ";");
            render.accept(this);
        }
        
        for (Def def : root.defs) {
            String defName = def.dotId();
            lines.add(defsName + "->" + defName + ";");
            def.accept(this);
        }
        
        return null;
    }
    
    @Override
    public Object visit(CssImport cssImport) {
        String name = cssImport.dotId();
        String s = "";
        if (cssImport.path == null) {
            s = "\\nnull";
        }
        else{
            s = "\\n" + cssImport.path.toString();
        }
        lines.add(name + "[ label = \"cssImport" + s + "\" ];");
        
        return null;
    }

    @Override
    public Object visit(UfeImport ufeImport) {
        String name = ufeImport.dotId();
        String s = "";
        if (ufeImport.id == null) {
            s = "\\nnull";
        }
        else{
            s = "\\n" + ufeImport.id;
        }
        String s1 = "";
        if (ufeImport.path == null) {
            s1 = "\\nnull";
        }
        else{
            s1 = "\\n" + ufeImport.path;
        }
        lines.add(name + "[ label = \"cssImport" + s + s1 + "\" ];");
        
        return null;
    }

    @Override
    public Object visit(Render render) {
        String name = render.dotId();
        String s = "";
        if (render.compId == null) {
            s = "\\nnull";
        }
        else{
            s = "\\n" + render.compId;
        }
        String s1 = "";
        if (render.divId == null) {
            s1 = "\\nnull";
        }
        else{
            s1 = "\\n" + render.divId;
        }
        lines.add(name + "[ label = \"cssImport" + s + s1 + "\" ];");
        
        return null;
    }
    
    @Override
    public Object visit(Def def) {
        String name = def.dotId();
        lines.add(name + "[ label = \"Def\\n" + def.componentId + "\" ];");
        
        for(Stmt e : def.stmts){
            lines.add(name + "->" + e.dotId() + ";");
            e.accept(this);
        }
        
        return null;
    }
    
    @Override
    public Object visit(Stmt.Decl decl) {
        String name = decl.dotId();
        lines.add(name + "[ label = \"Decl\" ];");
        
        for (Stmt.Decl.Item item : decl.items) {
            String itemName = item.toString().replace(".", "").replace("@", "").replace("$", "");
            lines.add(name + "->" + itemName + ";");
            lines.add(itemName + "[ label = \"" + item.id + "\" ];");
            lines.add(itemName + "->" + item.expr.dotId() + ";");
            item.expr.accept(this);
        }
        
        return null;
    }

    @Override
    public Object visit(Stmt.Assign assign) {
        String name = assign.dotId();
        lines.add(name + "[ label = \"Assign\\n" + assign.id + "\" ];");
        lines.add(name + "->" + assign.expr.dotId() + ";");
        
        assign.expr.accept(this);
        
        return null;
    }

    @Override
    public Object visit(Stmt.ArrayDecl arrayDecl) {
        String name = arrayDecl.dotId();
        
        if (arrayDecl.size == null) {
            lines.add(name + "[ label = \"ArrayDecl with values\" ];");
            for (Expr expr : arrayDecl.values) {
                String exprName = expr.dotId();
                lines.add(name + "->" + exprName + ";");
                expr.accept(this);
            }
        }
        else{
            lines.add(name + "[ label = \"ArrayDecl with index\" ];");
            String exprName = arrayDecl.size.dotId();
            lines.add(name + "->" + exprName + ";");
            arrayDecl.size.accept(this);
        }
        
        return null;
    }

    @Override
    public Object visit(Stmt.ArrayAssign arrayAssign) {
        String name = arrayAssign.dotId();
        lines.add(name + "[ label = \"ArrayAssign\\n" + arrayAssign.id + "[]\" ];");
        lines.add(name + "->" + arrayAssign.index.dotId() + ";");
        lines.add(name + "->" + arrayAssign.value.dotId() + ";");
        
        arrayAssign.index.accept(this);
        arrayAssign.value.accept(this);
        
        return null;
    }

    @Override
    public Object visit(Stmt.Print print) {
        String name = print.dotId();
        lines.add(name + "[ label = \"print\" ];");
        lines.add(name + "->" + print.expr.dotId() + ";");
        
        print.expr.accept(this);
        return null;
    }

    @Override
    public Object visit(Stmt._If _if) {
        String name = _if.dotId();
        lines.add(name + "[ label = \"SI\" ];");
        lines.add(name + "->" + _if.cond.dotId() + ";");
        _if.cond.accept(this);
        lines.add(name + "->" + _if.block.dotId() + ";");
        _if.block.accept(this);
        return null;
    }

    
    @Override
    public Object visit(Stmt._IfElse _ifElse) {
        String name = _ifElse.dotId();
        lines.add(name + "[ label = \"SI_SINO\" ];");
        lines.add(name + "->" + _ifElse.cond.dotId() + ";");
        _ifElse.cond.accept(this);
        lines.add(name + "->" + _ifElse.block.dotId() + ";");
        _ifElse.block.accept(this);
        
        //else
        if (_ifElse._else != null) {
            lines.add(name + "->" + _ifElse._else.dotId() + ";");
            _ifElse._else.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(_Else _else) {
        String name = _else.dotId();
        lines.add(name + "[ label = \"SINO\" ];");
        lines.add(name + "->" + _else.stmt.dotId() + ";");
        _else.stmt.accept(this);
        
        return null;
    }
    
    @Override
    public Object visit(Stmt.Block block){
        String name = block.dotId();
        lines.add(name + "[ label = \"BLOCK\" ];");
        
        for (Stmt stmt : block.stmts) {
            lines.add(name + "->" + stmt.dotId() + ";");
            stmt.accept(this);
        }
        
        return null;
    }
    
    @Override
    public Object visit(Stmt.Repeat repeat) {
        String name = repeat.dotId();
        lines.add(name + "[ label = \"REPETIR\" ];");
        lines.add(name + "->" + repeat.limit.dotId() + ";");
        repeat.limit.accept(this);
        lines.add(name + "->" + repeat.block.dotId() + ";");
        repeat.block.accept(this);
        
        return null;
    }

    @Override
    public Object visit(Stmt._While _while) {
        String name = _while.dotId();
        lines.add(name + "[ label = \"WHILE\" ];");
        lines.add(name + "->" + _while.cond.dotId() + ";");
        _while.cond.accept(this);
        lines.add(name + "->" + _while.block.dotId() + ";");
        _while.block.accept(this);
        
        return null;
    }
    
    @Override
    public Object visit(Expr.AtomicExpr expr) {
        String name = expr.dotId();
        String s = "";
        if (expr.value == null) {
            s = "\\nnull";
        }
        else{
            s = "\\n" + expr.value.getClass().getName() + "\\n" + expr.value.toString();
        }
        lines.add(name + "[ label = \"AtomicExpr" + s + "\" ];");
        
        return null;
    }

    
    @Override
    public Object visit(Expr.IdExpr expr) {
        String name = expr.dotId();
        String s = "\\n" + expr.id;
        lines.add(name + "[ label = \"IdExpr" + s + "\" ];");
        
        return null;
    }

    @Override
    public Object visit(Expr.ArrayIndexExpr expr) {
        String name = expr.dotId();
        String s = "\\n" + expr.id + "[]";
        lines.add(name + "[ label = \"ArrayIndexExpr" + s + "\" ];");
        lines.add(name + "->" + expr.index.dotId() + ";");
        expr.index.accept(this);
        
        return null;
    }
    
    @Override
    public Object visit(Expr.ULogicExpr expr) {
        String name = expr.dotId();
        String s = "\\n" + expr.op.toString();
        lines.add(name + "[ label = \"UExpr" + s + "\" ];");
        lines.add(name + "->" + expr.expr1.dotId() + ";");
        expr.expr1.accept(this);
        
        return null;
    }

    @Override
    public Object visit(Expr.UArithExpr expr) {
        String name = expr.dotId();
        String s = "\\n" + expr.op.toString();
        lines.add(name + "[ label = \"UExpr" + s + "\" ];");
        lines.add(name + "->" + expr.expr1.dotId() + ";");
        expr.expr1.accept(this);
        
        return null;
    }
    
    @Override
    public Object visit(Expr.BiCompExpr expr) {
        String name = expr.dotId();
        String s = "\\n" + expr.op.toString();
        lines.add(name + "[ label = \"BiCompExpr" + s + "\" ];");
        lines.add(name + "->" + expr.expr1.dotId() + ";");
        lines.add(name + "->" + expr.expr2.dotId() + ";");
        expr.expr1.accept(this);
        expr.expr2.accept(this);
        
        return null;
    }
    
    @Override
    public Object visit(Expr.BiLogicExpr expr) {
        String name = expr.dotId();
        String s = "\\n" + expr.op.toString();
        lines.add(name + "[ label = \"BiLogicExpr" + s + "\" ];");
        lines.add(name + "->" + expr.expr1.dotId() + ";");
        lines.add(name + "->" + expr.expr2.dotId() + ";");
        expr.expr1.accept(this);
        expr.expr2.accept(this);
        
        return null;
    }
    
    @Override
    public Object visit(Expr.BiArithExpr expr) {
        String name = expr.dotId();
        String s = "\\n" + expr.op.toString();
        lines.add(name + "[ label = \"BiArithExpr" + s + "\" ];");
        lines.add(name + "->" + expr.expr1.dotId() + ";");
        lines.add(name + "->" + expr.expr2.dotId() + ";");
        expr.expr1.accept(this);
        expr.expr2.accept(this);
        
        return null;
    }
    
    @Override
    public Object visit(Expr.BiConcatExpr expr) {
        String name = expr.dotId();
        String s = "\\n" + expr.op.toString();
        lines.add(name + "[ label = \"BiConcatExpr" + s + "\" ];");
        lines.add(name + "->" + expr.expr1.dotId() + ";");
        lines.add(name + "->" + expr.expr2.dotId() + ";");
        expr.expr1.accept(this);
        expr.expr2.accept(this);
        
        return null;
    }

    @Override
    public Object visit(Stmt.Return ret){
        String name = ret.dotId();
        lines.add(name + "[ label = \"Return" + "\" ];");
        
        for (Comp comp : ret.components) {
            lines.add(name + "->" + comp.dotId() + ";");
            comp.accept(this);
        }
        
        return null;
    }
    
    //Ufex
    @Override
    public Object visit(Comp.Default.Panel panel) {
        String name = panel.dotId();
        lines.add(name + "[ label = \"Panel" + "\" ];");
        
        for (Comp comp : panel.components) {
            lines.add(name + "->" + comp.dotId() + ";");
            comp.accept(this);
        }
        
        return null;
    }

    @Override
    public Object visit(Comp.Default.Text text) {
        String name = text.dotId();
        lines.add(name + "[ label = \"Text" + "\" ];");
        lines.add(name + "->" + text.contentGroup.dotId() + ";");
        text.contentGroup.accept(this);
        
        return null;
    }

    @Override
    public Object visit(Comp.Default.TextField textField) {
        String name = textField.dotId();
        lines.add(name + "[ label = \"TextField" + "\" ];");
        lines.add(name + "->" + textField.contentGroup.dotId() + ";");
        textField.contentGroup.accept(this);
        
        return null;
    }

    @Override
    public Object visit(Comp.Default.Button button) {
        String name = button.dotId();
        lines.add(name + "[ label = \"Button" + "\" ];");
        lines.add(name + "->" + button.contentGroup.dotId() + ";");
        button.contentGroup.accept(this);
        
        return null;
    }

    @Override
    public Object visit(Comp.Default.List list) {
        String name = list.dotId();
        lines.add(name + "[ label = \"List" + "\" ];");
        int i = 0;
        for(CntGroup contentGroup : list.items){
            lines.add(name + "Item" + i + "[ label = \"Item" + i + "\" ];");
            lines.add(name + "->" + name + "Item" + i + ";");
            lines.add(name + "Item" + i + "->" + contentGroup.dotId() + ";");
            contentGroup.accept(this);
            i++;
        }
        Object s = list._default.accept(this);
        if (s != null) {
            lines.add(name + "Default" + "[ label = \"Default" + "\" ];");
            lines.add(name + "->" + name + "Default" + ";");
            lines.add(name + "Default" + "->" + list._default.dotId() + ";");
        }
        
        return null;
    }

    @Override
    public Object visit(Comp.Default.Spinner spinner) {
        String name = spinner.dotId();
        lines.add(name + "[ label = \"Spinner" + "\" ];");
        lines.add(name + "->" + spinner._default.dotId() + ";");
        spinner._default.accept(this);
        
        return null;
    }

    @Override
    public Object visit(Comp.Default.Image image) {
        String name = image.dotId();
        lines.add(name + "[ label = \"Image" + "\" ];");
        
        return null;
    }

    @Override
    public Object visit(Comp.Custom custom) {
        String name = custom.dotId();
        String s = "\\n" + "custom.id";
        lines.add(name + "[ label = \"Custom" + s + "\" ];");
        
        return null;
    }

    @Override
    public Object visit(CntGroup cntGroup) {
        if (cntGroup.contents.size() < 1) {
            return null;
        }
        
        String name = cntGroup.dotId();
        lines.add(name + "[ label = \"ContentGroup" + "\" ];");
        
        StringBuilder sb = new StringBuilder();
        
        for (Cnt cnt : cntGroup.contents) {
            lines.add(name + "->" + cnt.dotId() + ";");
            Object obj = cnt.accept(this);
            
            sb.append(obj.toString());
        }
        
        return sb.toString();
    }

    @Override
    public Object visit(Cnt.PlainTxt plainText) {
        String name = plainText.dotId();
        String s = "\\n" + plainText.txt.replaceAll("\"", "");
        lines.add(name + "[ label = \"plainText" + s + "\" ];");
        
        return plainText.txt;
    }

    @Override
    public Object visit(Cnt.EmbUfe embUfe) {
        String name = embUfe.dotId();
        lines.add(name + "[ label = \"Embedded Ufe" + "\" ];");
        lines.add(name + "->" + embUfe.expr.dotId() + ";");
        embUfe.expr.accept(this);
        
        return "E";
    }

    @Override
    public Object visit(PropValue.IdProp idProp) {
        String name = idProp.dotId();
        String s = "\\n" + idProp.id;
        lines.add(name + "[ label = \"Id Prop" + s + "\" ];");
        
        return null;
    }

    @Override
    public Object visit(PropValue.AtomicProp atomicProp) {
        String name = atomicProp.dotId();
        String s = "\\n" + String.valueOf(atomicProp.value);
        lines.add(name + "[ label = \"Atomic Prop" + s + "\" ];");
        
        return null;
    }

    @Override
    public Object visit(PropValue.EmbProp embProp) {
        String name = embProp.dotId();
        lines.add(name + "[ label = \"Embedded Prop" + "\" ];");
        lines.add(name + "->" + embProp.expr.dotId() + ";");
        embProp.expr.accept(this);
        
        return null;
    }
}
