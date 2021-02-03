/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UfeNodes;

import UfeInterfaces.AstNode;
import UfeInterfaces.UfeVisitor;

/**
 *
 * @author Alvarez
 */
public abstract class Expr extends AstNode{
    //
    public static class IdExpr extends Expr{
        
        public String id;

        public IdExpr(String id) {
            this.id = id;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
    }
    
    
    public static class AtomicExpr extends Expr{
        
        public Object value; //can be null

        public AtomicExpr(Object value) {
            this.value = value;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
    }
    
    //
    public static class ArrayIndexExpr extends Expr{
        
        public String id;
        public Expr index;

        public ArrayIndexExpr(String id, Expr index) {
            this.id = id;
            this.index = index;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
    }

    /**
     * unary expre
     */
    public static class ULogicExpr extends Expr{

        public ULogicOp op;
        public Expr expr1;

        public ULogicExpr(ULogicOp op, Expr expr) {
            this.op = op;
            this.expr1 = expr;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
    }
    
    /**
     * unary expre
     */
    public static class UArithExpr extends Expr{

        public UArithOp op;
        public Expr expr1;

        public UArithExpr(UArithOp op, Expr expr) {
            this.op = op;
            this.expr1 = expr;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
    }
    
    /**
     * Binary expr
     */
    public static class BiCompExpr extends Expr{

        public Expr expr1;
        public BiCompOp op;
        public Expr expr2;

        public BiCompExpr(Expr expr1, BiCompOp op, Expr expr2) {
            this.expr1 = expr1;
            this.op = op;
            this.expr2 = expr2;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
    }
    
    /**
     * Binary expr
     */
    public static class BiLogicExpr extends Expr{

        public Expr expr1;
        public BiLogicOp op;
        public Expr expr2;

        public BiLogicExpr(Expr expr1, BiLogicOp op, Expr expr2) {
            this.expr1 = expr1;
            this.op = op;
            this.expr2 = expr2;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
    }
    
    /**
     * Binary expr
     */
    public static class BiArithExpr extends Expr{

        public Expr expr1;
        public BiArithOp op;
        public Expr expr2;

        public BiArithExpr(Expr expr1, BiArithOp op, Expr expr2) {
            this.expr1 = expr1;
            this.op = op;
            this.expr2 = expr2;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
    }
    
    /**
     * Binary expr
     */
    public static class BiConcatExpr extends Expr{

        public Expr expr1;
        public BiConcatOp op;
        public Expr expr2;

        public BiConcatExpr(Expr expr1, BiConcatOp op, Expr expr2) {
            this.expr1 = expr1;
            this.op = op;
            this.expr2 = expr2;
        }
        
        @Override
        public Object accept(UfeVisitor v) {
            return v.visit(this);
        }
    }
    
    /**
     * Unary logic operator
     */
    public enum ULogicOp{
        NOT;
    }
    
    /**
     * Unary arithmetic operator
     */
    public enum UArithOp{
        UMINUS;
    }
    
    /**
     * Binary comparator operator
     */
    public enum BiCompOp{
        LESS, GREATER, LESS_EQ, GREATER_EQ, EQ_EQ, NOT_EQ;
    }
    
    /**
     * Binary logic operator
     */
    public enum BiLogicOp{
        AND, OR, XOR;
    }
    
    /**
     * Binary arithmetic operator
     */
    public enum BiArithOp{
        MINUS, MULT, DIV, POW;
    }

    /**
     * CHAPUZ MEDIO PARA SEPARAR LA IMPLEMENTACION DE CONCATENACION
     * Binary concatenation operator
     * Plus. It can be used as regular PLUS
     */
    public enum BiConcatOp{
        PLUS
    }
}
    
