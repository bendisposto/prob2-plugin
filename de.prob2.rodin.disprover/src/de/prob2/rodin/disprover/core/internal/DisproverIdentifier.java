package de.prob2.rodin.disprover.core.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;

import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.animator.domainobjects.EventB;

/**
 * Helper class for the Disprover, containing an identifier and its type.
 * <p>
 * 
 * As Nodes in the Syntax Tree can only have one parent, the getter methods
 * always create new Objects, rather than handing out references.
 * <p>
 * 
 * Types (vs. variables and constants ) have a name, but the type is null.
 * 
 * @author jastram
 */
public class DisproverIdentifier {

	private final String name;
	private final Type type;
	private final boolean givenSet;

	public DisproverIdentifier(String name, Type type, boolean givenSet) {
		this.givenSet = givenSet;
		this.name = name;
		this.type = type;
	}

	public boolean isGivenSet() {
		return givenSet;
	}

	public boolean isPrimedVariable() {
		return name.endsWith("'");
	}

	private EventB typeToPExpression(Type type) {
		String exS = type.toExpression().toStringFullyParenthesized();
		Set<IFormulaExtension> extensions = type.getFactory().getExtensions();
		return new EventB(exS, extensions);
	}

	public String getName() {
		return name;
	}

	public List<TIdentifierLiteral> getId() {
		return stringToIdentifierLiteralList(name);
	}

	public AIdentifierExpression getIdExpression() {
		return new AIdentifierExpression(getId());
	}

	public EventB getType() {
		if (type == null)
			return null;
		return typeToPExpression(type);
	}

	private List<TIdentifierLiteral> stringToIdentifierLiteralList(String name) {
		List<TIdentifierLiteral> identifiers = new ArrayList<TIdentifierLiteral>();
		identifiers.add(new TIdentifierLiteral(name));
		return identifiers;
	}

	@Override
	public String toString() {
		return name + "(" + type + ")";
	}

}
