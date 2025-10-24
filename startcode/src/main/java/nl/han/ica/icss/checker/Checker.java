package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();

        checkStylesheet(ast.root);

    }

    private void checkStylesheet(Stylesheet sheet) {
        for (ASTNode child : sheet.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Stylerule) {
                checkStylerule((Stylerule) child);
            }
        }
    }

    private void checkStylerule(Stylerule rule) {
        variableTypes.addFirst(new HashMap<>());

        for (ASTNode child : rule.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            } else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            }
        }

        variableTypes.removeFirst();
    }

    private void checkVariableAssignment(VariableAssignment assignment) {
        ExpressionType type = null;

        if (assignment.expression instanceof PixelLiteral) type = ExpressionType.PIXEL;
        else if (assignment.expression instanceof PercentageLiteral) type = ExpressionType.PERCENTAGE;
        else if (assignment.expression instanceof ColorLiteral) type = ExpressionType.COLOR;
        else if (assignment.expression instanceof BoolLiteral) type = ExpressionType.BOOL;
        else if (assignment.expression instanceof ScalarLiteral) type = ExpressionType.SCALAR;

        variableTypes.getFirst().put(assignment.name.name, type);
    }
    


    
}
