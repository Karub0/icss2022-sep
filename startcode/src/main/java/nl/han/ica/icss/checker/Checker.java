package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;


public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
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
        ExpressionType type = getExpressionType(assignment.expression);
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

    private ExpressionType getExpressionType(ASTNode node) {
        if (node instanceof ColorLiteral) return ExpressionType.COLOR;
        if (node instanceof PixelLiteral) return ExpressionType.PIXEL;
        if (node instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (node instanceof ScalarLiteral) return ExpressionType.SCALAR;
        if (node instanceof BoolLiteral) return ExpressionType.BOOL;

        if (node instanceof VariableReference) {
            VariableReference reference = (VariableReference) node;
            for (HashMap<String, ExpressionType> scope : variableTypes) {
                if (scope.containsKey(reference.name)) {
                    return scope.get(reference.name);
                }
            }
            reference.setError("Variable '" + reference.name + "' not defined.");
            return ExpressionType.UNDEFINED;
        }

        if (node instanceof Operation) {
            Operation operation = (Operation) node;
            ExpressionType left = getExpressionType(operation.lhs);
            ExpressionType right = getExpressionType(operation.rhs);

            if (left == ExpressionType.COLOR || right == ExpressionType.COLOR) {
                operation.setError("Color cannot be used in operations.");
                return ExpressionType.UNDEFINED;
            }

            if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
                if (left == right && (left == ExpressionType.PIXEL || left == ExpressionType.PERCENTAGE || left == ExpressionType.SCALAR)) {
                    return left;
                } else {
                    operation.setError("Operand + or - must have same type.");
                    return ExpressionType.UNDEFINED;
                }
            }

            if (operation instanceof MultiplyOperation) {
                if (left == ExpressionType.SCALAR && (right == ExpressionType.PIXEL || right == ExpressionType.PERCENTAGE)) {
                    return right;
                }
                if (right == ExpressionType.SCALAR && (left == ExpressionType.PIXEL || left == ExpressionType.PERCENTAGE)) {
                    return left;
                }
                if (left == ExpressionType.SCALAR && right == ExpressionType.SCALAR) {
                    return ExpressionType.SCALAR;
                }

                operation.setError("One operand of * must be scalar.");
                return ExpressionType.UNDEFINED;
            }
        }

        return ExpressionType.UNDEFINED;
    }

    private void checkIfClause(IfClause ifClause) {
        ExpressionType type = getExpressionType(ifClause.conditionalExpression);
        if (type != ExpressionType.BOOL) {
            ifClause.setError("If must be boolean.");
        }
        for (ASTNode child : ifClause.body) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            } else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            } else if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            }
        }

        if (ifClause.elseClause != null) {
            checkElseClause(ifClause.elseClause);
        }
    }

    private void checkElseClause(ElseClause elseClause) {
        for (ASTNode child : elseClause.body) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            } else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            } else if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            }
        }
    }
}
