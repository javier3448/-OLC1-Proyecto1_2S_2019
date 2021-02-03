/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeNodes;

import UfeInterfaces.AstNode;
import UfeInterfaces.UfeVisitor;
import java.util.LinkedList;

/**
 *
 * @author Alvarez
 */
public abstract class Stmt extends AstNode{

    public static class Assign extends Stmt{
        public String id;
        public Expr expr;

        public Assign(String id, Expr expr) {
            this.id = id;
            this.expr = expr;
        }

        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
        
        
    }

    public static class Decl extends Stmt{
        //Pareja de string y expr, sin funcionalidad, expr no puede ser null
        public static class Item{
            public String id;
            public Expr expr;

            public Item(String id, Expr expr) {
                this.id = id;
                this.expr = expr;
            }
        }
        
        public LinkedList<Item> items;
        
        public Decl(LinkedList<Item> items){
            this.items = items;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
        
    }
    
    public static class ArrayAssign extends Stmt{
        public String id;
        public Expr index;
        public Expr value;

        public ArrayAssign(String id, Expr index, Expr value) {
            this.id = id;
            this.index = index;
            this.value = value;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
    }

    public static class ArrayDecl extends Stmt{
        public String id;
        public Expr size;
        public LinkedList<Expr> values; 

        public ArrayDecl(String id, LinkedList<Expr> values) {
            this.id = id;
            this.values = values;
        }

        public ArrayDecl(String id, Expr size) {
            this.id = id;
            this.size = size;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
    }
    
    public static class _If extends Stmt{
        
        public Expr cond;
        public Block block;
        
        public _If(Expr cond, Block block) {
            this.cond = cond;
            this.block = block;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
        
    }
    
    public static class _IfElse extends Stmt{
        
        public Expr cond;
        public Block block;
        public _Else _else;

        public _IfElse(Expr cond, Block block, _Else _else) {
            this.cond = cond;
            this.block = block;
            this._else = _else;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
        
    }
    
    public static class Block extends Stmt{

        public LinkedList<Stmt> stmts;

        public Block(LinkedList<Stmt> stmts) {
            this.stmts = stmts;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
        
    }
    
    public static class Repeat extends Stmt{

        public Expr limit;
        public Block block;

        public Repeat(Expr limit, Block block) {
            this.limit = limit;
            this.block = block;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
        
    }
    
    public static class _While extends Stmt{
        public Expr cond;
        public Block block;

        public _While(Expr cond, Block block) {
            this.cond = cond;
            this.block = block;
        }

        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
        
    }
    
    public static class Return extends Stmt{
        public LinkedList<Ufex.Comp> components;

        public Return(LinkedList<Ufex.Comp> components) {
            this.components = components;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
        
    }
    
    public static class Print extends Stmt{

        public Expr expr;

        public Print(Expr expr) {
            this.expr = expr;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
    }
}
