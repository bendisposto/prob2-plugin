package de.prob2.rodin.disprover.core.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.seqprover.IProverSequent;

import de.be4.classicalb.core.parser.node.AAxiomsContextClause;
import de.be4.classicalb.core.parser.node.AConstantsContextClause;
import de.be4.classicalb.core.parser.node.ADeferredSetSet;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AMemberPredicate;
import de.be4.classicalb.core.parser.node.ASetsContextClause;
import de.be4.classicalb.core.parser.node.PContextClause;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PSet;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.animator.domainobjects.EventB;
import de.prob2.rodin.disprover.core.internal.DisproverIdentifier;

public class DisproverContextCreator {
	public static AEventBContextParseUnit createDisproverContext(
			IProverSequent sequent, Set<EventB> theHyps) {
		return createDisproverContext(sequent.typeEnvironment(), theHyps);
	}

	public static AEventBContextParseUnit createDisproverContext(
			ITypeEnvironment typeEnvironment, Set<EventB> theHyps) {
		AEventBContextParseUnit context = new AEventBContextParseUnit();
		context.setName(new TIdentifierLiteral("DisproverContext"));

		final List<PContextClause> contextClauses = new ArrayList<PContextClause>();

		// collecting variables, sets and (type-)invariants
		final AConstantsContextClause constantsClause = new AConstantsContextClause();
		final List<PExpression> constantsIdentifiers = new ArrayList<PExpression>();

		final ASetsContextClause setsContextClause = new ASetsContextClause();
		final List<PSet> sets = new ArrayList<PSet>();

		final AAxiomsContextClause axiomsContextClause = new AAxiomsContextClause();
		final List<PPredicate> axioms = new ArrayList<PPredicate>();

		// Iterate over the type environment to construct a typing context
		IIterator typeIterator = typeEnvironment.getIterator();

		while (typeIterator.hasNext()) {
			typeIterator.advance();

			DisproverIdentifier id = new DisproverIdentifier(
					typeIterator.getName(), typeIterator.getType(),
					typeIterator.isGivenSet());

			// sets are added to the context, vars to the model
			if (id.isGivenSet()) {
				sets.add(new ADeferredSetSet(id.getId()));
			} else {
				// might not be necessary for constants
				// if (!id.isPrimedVariable()) {
				constantsIdentifiers.add(new AIdentifierExpression(id.getId()));

				AIdentifierExpression idExpression = id.getIdExpression();
				EventB type = id.getType();

				axioms.add(new AMemberPredicate(idExpression,
						(PExpression) type.getAst()));
				// }
			}
		}

		// store hypothesis as axioms
		for (EventB predicate : theHyps) {
			axioms.add((PPredicate) predicate.getAst());
		}

		axiomsContextClause.setPredicates(axioms);
		contextClauses.add(axiomsContextClause);

		constantsClause.setIdentifiers(constantsIdentifiers);
		contextClauses.add(constantsClause);

		setsContextClause.setSet(sets);
		contextClauses.add(setsContextClause);

		context.setContextClauses(contextClauses);

		return context;
	}

}
