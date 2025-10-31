package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

public class Generator {

	public String generate(AST ast) {
		return generateStylesheet(ast.root, 0);
	}

	private String generateStylesheet(Stylesheet sheet, int indentLevel) {
		StringBuilder builder = new StringBuilder();

		for (ASTNode child : sheet.getChildren()) {
			if (child instanceof Stylerule) {
				builder.append(generateStylerule((Stylerule) child, indentLevel)).append("\n");
			}
		}

		return builder.toString();
	}

	private String generateStylerule(Stylerule rule, int indentLevel) {
		StringBuilder builder = new StringBuilder();
		String indent = "  ".repeat(indentLevel);
		String indent2 = "  ".repeat(indentLevel + 1);

		// Voeg de selectors toe gescheiden door komma
		for (int i = 0; i < rule.selectors.size(); i++) {
			builder.append(indent).append(rule.selectors.get(i).toString());
			if (i < rule.selectors.size() - 1) {
				builder.append(", ");
			}
		}
		builder.append(" {\n");

		for (ASTNode child : rule.body) {
			if (child instanceof Declaration) {
				// Genereer CSS
				builder.append(indent2).append(generateDeclaration((Declaration) child)).append("\n");

			} else if (child instanceof Stylerule) {
				// Genereer geneste regels
				builder.append(generateStylerule((Stylerule) child, indentLevel + 1)).append("\n");
			}
		}

		builder.append(indent).append("}");
		return builder.toString();
	}

	private String generateDeclaration(Declaration declaration) {
		return declaration.property.name + ": " + literalToString(declaration.expression) + ";";
	}

	// Zet een Literal of expression om naar een string in CSS
	private String literalToString(ASTNode expression) {
		if (expression instanceof PixelLiteral) {
			return ((PixelLiteral) expression).value + "px";

		} else if (expression instanceof PercentageLiteral) {
			return ((PercentageLiteral) expression).value + "%";

		} else if (expression instanceof ScalarLiteral) {
			return String.valueOf(((ScalarLiteral) expression).value);

		} else if (expression instanceof ColorLiteral) {
			return ((ColorLiteral) expression).value;

		} else if (expression instanceof BoolLiteral) {
			return String.valueOf(((BoolLiteral) expression).value).toLowerCase();

		} else if (expression instanceof VariableReference) {
			return ((VariableReference) expression).name;

		} else if (expression instanceof Operation) {
			Operation operation = (Operation) expression;
			String left = literalToString(operation.lhs);
			String right = literalToString(operation.rhs);
			if (operation instanceof nl.han.ica.icss.ast.operations.AddOperation) {
				return left + " + " + right;

			} else if (operation instanceof nl.han.ica.icss.ast.operations.SubtractOperation) {
				return left + " - " + right;

			} else if (operation instanceof nl.han.ica.icss.ast.operations.MultiplyOperation) {
				return left + " * " + right;
			}
		}
		return "";
	}
}
