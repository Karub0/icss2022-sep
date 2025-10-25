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
        variableTypes.addFirst(new HashMap<>());

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
            } else if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
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

    private void checkDeclaration(Declaration declaration) {
        ExpressionType type = getExpressionType(declaration.expression);

        String prop = declaration.property.name;
        if ((prop.equals("width") || prop.equals("height"))
                && !(type == ExpressionType.PIXEL || type == ExpressionType.PERCENTAGE || type == ExpressionType.SCALAR)) {
            declaration.setError("Property '" + prop + "' expects numeric value.");
        }

        if ((prop.equals("color") || prop.equals("background-color"))
                && type != ExpressionType.COLOR) {
            declaration.setError("Property '" + prop + "' expects color value.");
        }
    }

    private ExpressionType getExpressionType(ASTNode type) {
        if (type instanceof ColorLiteral) return ExpressionType.COLOR;
        if (type instanceof PixelLiteral) return ExpressionType.PIXEL;
        if (type instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (type instanceof ScalarLiteral) return ExpressionType.SCALAR;
        if (type instanceof BoolLiteral) return ExpressionType.BOOL;

        if (type instanceof VariableReference) {
            VariableReference reference = (VariableReference) type;

            for (int i = 0; i < variableTypes.getSize(); i++) {
                HashMap<String, ExpressionType> scope = variableTypes.get(i);
                if (scope.containsKey(reference.name)) {
                    return scope.get(reference.name);
                }
            }
        }

        if (type instanceof Operation) {
            Operation operation = (Operation) type;
            ExpressionType left = getExpressionType(operation.lhs);
            ExpressionType right = getExpressionType(operation.rhs);

            if (left == ExpressionType.COLOR || right == ExpressionType.COLOR) {
                operation.setError("Color cannot be used in operations.");
                return ExpressionType.UNDEFINED;
            }

            if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
                if (left != right) {
                    operation.setError("Operand + or - must have same type.");
                    return ExpressionType.UNDEFINED;
                }
                return left;

            } else if (operation instanceof MultiplyOperation) {
                if (left == ExpressionType.SCALAR) return right;
                if (right == ExpressionType.SCALAR) return left;
                operation.setError("One operand of * must be scalar.");
                return ExpressionType.UNDEFINED;
            }
        }

        return ExpressionType.UNDEFINED;
    }





}
