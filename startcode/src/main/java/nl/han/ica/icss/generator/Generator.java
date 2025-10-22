package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;

public class Generator {

	public String generate(AST ast) {
        return generateStylesheet(ast.root);


	}

    private String generateStylesheet(Stylesheet node) {
		return generateStylerule((Stylerule)node.getChildren().get(0));
    }

	private String generateStylerule(Stylerule node) {
		return node.selectors.get(0).toString()
				+ "{\n"
				+ generateDeclaration((Declaration)node.body.get(0))
		        + "}";
	}

	private String generateDeclaration(Declaration node) {
		return "h";
	}
}
