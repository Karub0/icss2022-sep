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
		// Afhankelijk van het type selector het juiste object aanmaken
		Selector selector = null;
		if (ctx.LOWER_IDENT() != null) {
			selector = new TagSelector(ctx.LOWER_IDENT().getText());
		} else if (ctx.CLASS_IDENT() != null) {
			selector = new ClassSelector(ctx.CLASS_IDENT().getText());
		} else if (ctx.ID_IDENT() != null) {
			selector = new nl.han.ica.icss.ast.selectors.IdSelector(ctx.ID_IDENT().getText());
		}
		currentContainer.push(selector);
	}

	@Override
	public void exitSelector(ICSSParser.SelectorContext ctx) {
		Selector selector = (Selector) currentContainer.pop();
		((Stylerule) currentContainer.peek()).addChild(selector);
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration();
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = (Declaration) currentContainer.pop();
		((ASTNode) currentContainer.peek()).addChild(declaration);
	}

	@Override
	public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
		PropertyName property = new PropertyName(ctx.getText());
		((Declaration) currentContainer.peek()).addChild(property);
	}

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment assignment = new VariableAssignment();
		VariableReference name = new VariableReference(ctx.CAPITAL_IDENT().getText());
		assignment.addChild(name);
		currentContainer.push(assignment);
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment assignment = (VariableAssignment) currentContainer.pop();
		((ASTNode) currentContainer.peek()).addChild(assignment);
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
	public void enterElseBody(ICSSParser.ElseBodyContext ctx) {
		ElseClause elseClause = new ElseClause();
		currentContainer.push(elseClause);
	}

	@Override
	public void exitElseBody(ICSSParser.ElseBodyContext ctx) {
		ElseClause elseClause = (ElseClause) currentContainer.pop();
		((IfClause) currentContainer.peek()).addChild(elseClause);
	}

	@Override
	public void enterAddSub(ICSSParser.AddSubContext ctx) {
		// Controleer of het gaat om + of -
		Operation operation = null;
		if (ctx.PLUS() != null) {
			operation = new AddOperation();
		} else if (ctx.MIN() != null) {
			operation = new SubtractOperation();
		}
		currentContainer.push(operation);
	}

	@Override
	public void exitAddSub(ICSSParser.AddSubContext ctx) {
		Operation operation = (Operation) currentContainer.pop();
		((ASTNode) currentContainer.peek()).addChild(operation);
	}

	@Override
	public void enterMul(ICSSParser.MulContext ctx) {
		MultiplyOperation operation = new MultiplyOperation();
		currentContainer.push(operation);
	}

	@Override
	public void exitMul(ICSSParser.MulContext ctx) {
		MultiplyOperation operation = (MultiplyOperation) currentContainer.pop();
		((ASTNode) currentContainer.peek()).addChild(operation);
	}

	// Lege methodes voor grammar regels die niets doen
	@Override
	public void enterSingleTerm(ICSSParser.SingleTermContext ctx) {

	}

	@Override
	public void enterSingleFactor(ICSSParser.SingleFactorContext ctx) {

	}

	@Override
	public void enterLiteralFactor(ICSSParser.LiteralFactorContext ctx) {
		// Zoekt het type literal en maakt het juiste object aan
		Literal literal = null;

		if (ctx.literal().PIXELSIZE() != null) {
			literal = new PixelLiteral(ctx.literal().PIXELSIZE().getText());
		} else if (ctx.literal().PERCENTAGE() != null) {
			literal = new PercentageLiteral(ctx.literal().PERCENTAGE().getText());
		} else if (ctx.literal().COLOR() != null) {
			literal = new ColorLiteral(ctx.literal().COLOR().getText());
		} else if (ctx.literal().TRUE() != null || ctx.literal().FALSE() != null) {
			literal = new BoolLiteral(ctx.literal().getText());
		} else if (ctx.literal().SCALAR() != null) {
			literal = new ScalarLiteral(ctx.literal().SCALAR().getText());
		}

		((ASTNode) currentContainer.peek()).addChild(literal);
	}

	@Override
	public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
		VariableReference ref = new VariableReference(ctx.CAPITAL_IDENT().getText());
		((ASTNode) currentContainer.peek()).addChild(ref);
	}


}