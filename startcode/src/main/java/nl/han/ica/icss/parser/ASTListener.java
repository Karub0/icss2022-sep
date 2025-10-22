package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.datastructures.HANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;


/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = new Stylesheet();
		currentContainer.push(stylesheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = (Stylesheet) currentContainer.pop();
		ast.root = stylesheet;
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule rule = new Stylerule();
		currentContainer.push(rule);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule rule = (Stylerule) currentContainer.pop();
		((Stylesheet) currentContainer.peek()).addChild(rule);
	}

	@Override
	public void enterSelector(ICSSParser.SelectorContext ctx) {
		Selector selector = null;
		if (ctx.LOWER_IDENT() != null) {
			selector = new TagSelector(ctx.LOWER_IDENT().getText());
		} else if (ctx.ID_IDENT() != null) {
			selector = new IdSelector(ctx.ID_IDENT().getText());
		} else if (ctx.CLASS_IDENT() != null) {
			selector = new ClassSelector(ctx.CLASS_IDENT().getText());
		}
		if (selector != null) {
			((Stylerule) currentContainer.peek()).addChild(selector);
		}
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration();
		declaration.property = new PropertyName(ctx.propertyName().getText());
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = (Declaration) currentContainer.pop();
		((Stylerule) currentContainer.peek()).addChild(declaration);
	}

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment assignment = new VariableAssignment();
		assignment.name = new VariableReference(ctx.CAPITAL_IDENT().getText());
		currentContainer.push(assignment);
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment assignment = (VariableAssignment) currentContainer.pop();
		((ASTNode) currentContainer.peek()).addChild(assignment);
	}

	@Override
	public void enterLiteral(ICSSParser.LiteralContext ctx) {
		Literal literal = null;

		if (ctx.PIXELSIZE() != null) {
			literal = new PixelLiteral(ctx.PIXELSIZE().getText().replace("px", ""));
		} else if (ctx.PERCENTAGE() != null) {
			literal = new PercentageLiteral(ctx.PERCENTAGE().getText().replace("%", ""));
		} else if (ctx.SCALAR() != null) {
			literal = new ScalarLiteral(ctx.SCALAR().getText());
		} else if (ctx.COLOR() != null) {
			literal = new ColorLiteral(ctx.COLOR().getText());
		} else if (ctx.TRUE() != null) {
			literal = new BoolLiteral(true);
		} else if (ctx.FALSE() != null) {
			literal = new BoolLiteral(false);
		}

		if (literal != null) {
			currentContainer.push(literal);
		}
	}

	@Override
	public void exitLiteral(ICSSParser.LiteralContext ctx) {
	}

	@Override
	public void enterIfClause(ICSSParser.IfClauseContext ctx) {
		IfClause ifClause = new IfClause();
		currentContainer.push(ifClause);
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		IfClause ifClause = (IfClause) currentContainer.pop();
		((ASTNode) currentContainer.peek()).addChild(ifClause);
	}

	@Override
	public void exitAddSub(ICSSParser.AddSubContext ctx) {
		Expression rhs = (Expression) currentContainer.pop();
		Expression lhs = (Expression) currentContainer.pop();

		Operation op = (ctx.PLUS() != null)
				? new AddOperation()
				: new SubtractOperation();

		op.lhs = lhs;
		op.rhs = rhs;
		currentContainer.push(op);
	}

	@Override
	public void exitMul(ICSSParser.MulContext ctx) {
		Expression rhs = (Expression) currentContainer.pop();
		Expression lhs = (Expression) currentContainer.pop();

		MultiplyOperation op = new MultiplyOperation();
		op.lhs = lhs;
		op.rhs = rhs;
		currentContainer.push(op);
	}
	

}