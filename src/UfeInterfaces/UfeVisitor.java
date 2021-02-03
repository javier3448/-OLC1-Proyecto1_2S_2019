/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeInterfaces;

import UfeNodes.Def;
import UfeNodes.Stmt.*;
import UfeNodes.Expr.*;
import UfeNodes.*;


/**
 *
 * @author Alvarez
 */
public interface UfeVisitor {
    
    public Object visit(Root root);
    public Object visit(CssImport cssImport);
    public Object visit(UfeImport ufeImport);
    public Object visit(Render render);
    public Object visit(Def def);
    public Object visit(Decl decl);
    public Object visit(Assign assign);
    public Object visit(ArrayDecl arrayDecl);
    public Object visit(ArrayAssign arrayAssign);
    public Object visit(Print print);
    public Object visit(Stmt.Return ret);
    public Object visit(Block block);
    public Object visit(_If _if);
    public Object visit(_IfElse _ifElse);
    public Object visit(_Else _else);
    public Object visit(Stmt.Repeat repeat);
    public Object visit(Stmt._While _while);
    public Object visit(AtomicExpr expr);
    public Object visit(IdExpr expr);
    public Object visit(ArrayIndexExpr expr);
    public Object visit(ULogicExpr expr);
    public Object visit(UArithExpr expr);
    public Object visit(BiCompExpr expr);
    public Object visit(BiLogicExpr expr);
    public Object visit(BiArithExpr expr);
    public Object visit(BiConcatExpr expr);
    
    //Ufex
    public Object visit(Ufex.Comp.Default.Panel panel);
    public Object visit(Ufex.Comp.Default.Text text);
    public Object visit(Ufex.Comp.Default.TextField textField);
    public Object visit(Ufex.Comp.Default.Button button);
    public Object visit(Ufex.Comp.Default.List list);
    public Object visit(Ufex.Comp.Default.Spinner spinner);
    public Object visit(Ufex.Comp.Default.Image image);
    public Object visit(Ufex.Comp.Custom custom);
    public Object visit(Ufex.CntGroup cntGroup);
    public Object visit(Ufex.Cnt.PlainTxt plainText);
    public Object visit(Ufex.Cnt.EmbUfe embUfe);
    public Object visit(Ufex.PropValue.IdProp idProp);
    public Object visit(Ufex.PropValue.AtomicProp atomicProp);
    public Object visit(Ufex.PropValue.EmbProp embProp);
}
