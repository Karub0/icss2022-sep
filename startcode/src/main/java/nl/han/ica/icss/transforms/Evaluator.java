package nl.han.ica.icss.transforms;

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

    private void evaluateDeclaration(Declaration declaration) {
        declaration.expression = evaluateExpression(declaration.expression);
    }

    private Literal evaluateExpression(Expression expression) {
        if (expression instanceof Literal) {
            return (Literal) expression;
        } else if (expression instanceof VariableReference) {
            return resolveVariable((VariableReference) expression);
        } else if (expression instanceof Operation) {
            return evaluateOperation((Operation) expression);
        }
        return null;
    }

    private Literal resolveVariable(VariableReference reference) {
        for (HashMap<String, Literal> scope : variableValues) {
            if (scope.containsKey(reference.name)) {
                return scope.get(reference.name);
            }
        }
        return new ScalarLiteral(0);
    }

    private Literal evaluateOperation(Operation operation) {
        Literal left = evaluateExpression(operation.lhs);
        Literal right = evaluateExpression(operation.rhs);

        if (operation instanceof AddOperation) {
            return evaluateAddSub(left, right, true);
        } else if (operation instanceof SubtractOperation) {
            return evaluateAddSub(left, right, false);
        } else if (operation instanceof MultiplyOperation) {
            return evaluateMultiply(left, right);
        }
        return null;
    }

    private Literal evaluateAddSub(Literal left, Literal right, boolean isAdd) {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            int result = ((PixelLiteral) left).value + (isAdd ? ((PixelLiteral) right).value : -((PixelLiteral) right).value);
            return new PixelLiteral(result);

        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            int result = ((PercentageLiteral) left).value + (isAdd ? ((PercentageLiteral) right).value : -((PercentageLiteral) right).value);
            return new PercentageLiteral(result);

        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            int result = ((ScalarLiteral) left).value + (isAdd ? ((ScalarLiteral) right).value : -((ScalarLiteral) right).value);
            return new ScalarLiteral(result);
        }
        return left;
    }

    private Literal evaluateMultiply(Literal left, Literal right) {
        if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            int result = ((ScalarLiteral) left).value * ((ScalarLiteral) right).value;
            return new ScalarLiteral(result);

        } else if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {
            int result = ((ScalarLiteral) left).value * ((PixelLiteral) right).value;
            return new PixelLiteral(result);

        } else if (left instanceof PixelLiteral && right instanceof ScalarLiteral) {
            int result = ((PixelLiteral) left).value * ((ScalarLiteral) right).value;
            return new PixelLiteral(result);

        } else if (left instanceof ScalarLiteral && right instanceof PercentageLiteral) {
            int result = ((ScalarLiteral) left).value * ((PercentageLiteral) right).value;
            return new PercentageLiteral(result);

        } else if (left instanceof PercentageLiteral && right instanceof ScalarLiteral) {
            int result = ((PercentageLiteral) left).value * ((ScalarLiteral) right).value;
            return new PercentageLiteral(result);
        }
        return left;
    }
}







