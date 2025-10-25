package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();
        variableValues.push(new HashMap<>());

        evaluateStylesheet(ast.root);
    }

    private void evaluateStylesheet(Stylesheet sheet) {
        for (ASTNode child : sheet.getChildren()) {
            if (child instanceof VariableAssignment) {
                evaluateVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Stylerule) {
                evaluateStylerule((Stylerule) child);
            }
        }
    }

    private void evaluateVariableAssignment(VariableAssignment assignment) {
        Literal literal = evaluateExpression(assignment.expression);
        variableValues.peek().put(assignment.name.name, literal);
    }

    private void evaluateStylerule(Stylerule rule) {
        variableValues.push(new HashMap<>());

        ListIterator<ASTNode> iterator = rule.getChildren().listIterator();
        while (iterator.hasNext()) {
            ASTNode child = iterator.next();

            if (child instanceof Declaration) {
                evaluateDeclaration((Declaration) child);
            } else if (child instanceof VariableAssignment) {
                evaluateVariableAssignment((VariableAssignment) child);
                iterator.remove();
            }
        }

        variableValues.pop();
    }




}
