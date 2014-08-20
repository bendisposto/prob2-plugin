package de.prob2.rodin.disprover.core;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IEventBProject;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.prob.animator.domainobjects.EventB;
import de.prob.formula.TranslationVisitor;
import de.prob.prolog.output.PrologTermStringOutput;
import de.prob2.rodin.disprover.core.internal.DisproverCommand;
import de.prob2.rodin.disprover.core.internal.ICounterExample;
import de.prob2.rodin.disprover.core.translation.DisproverContextCreator;

public class DisproverReasoner implements IReasoner {

	static final String DISPROVER_CONTEXT = "disprover_context";

	private static final String DISPROVER_REASONER_NAME = "de.prob.eventb.disprover.core.disproverReasoner";

	Logger logger = LoggerFactory.getLogger(DisproverReasoner.class);

	private final int timeoutFactor;

	public DisproverReasoner() {
		this(1);
	}

	public DisproverReasoner(int timeoutFactor) {
		this.timeoutFactor = timeoutFactor;
	}

	@Override
	public String getReasonerID() {
		return DISPROVER_REASONER_NAME;
	}

	@Override
	public IReasonerOutput apply(final IProverSequent sequent,
			final IReasonerInput input, final IProofMonitor pm) {
		try {
			DisproverReasonerInput disproverInput = (DisproverReasonerInput) input;
			ICounterExample ce = evaluateSequent(sequent, disproverInput,
					timeoutFactor, pm);
			return createDisproverResult(ce, sequent, input);
		} catch (RodinDBException e) {
			logger.warn("Rodin DB Exception", e);
			return ProverFactory.reasonerFailure(this, input, e.getMessage());
		} catch (InterruptedException e) {
			return ProverFactory.reasonerFailure(this, input, e.getMessage());

		}
	}

	private ICounterExample evaluateSequent(final IProverSequent sequent,
			final DisproverReasonerInput disproverInput, int timeoutFactor,
			IProofMonitor pm) throws RodinDBException, InterruptedException {
		// Logger.info("Calling Disprover on Sequent");

		Set<EventB> allHypotheses = new HashSet<EventB>();
		Set<EventB> selectedHypotheses = new HashSet<EventB>();
		// StringBuilder hypothesesString = new StringBuilder();
		for (Predicate predicate : sequent.hypIterable()) {
			allHypotheses.add(translateToEvalElement(predicate));
			// hypothesesString.append(predicateToProlog(predicate));
			// hypothesesString.append(" & ");
		}

		// StringBuilder hypothesesString = new StringBuilder();
		for (Predicate predicate : sequent.selectedHypIterable()) {
			selectedHypotheses.add(translateToEvalElement(predicate));
			// hypothesesString.append(predicateToProlog(predicate));
			// hypothesesString.append(" & ");
		}

		/*
		 * if (hypothesesString.length() == 0) {
		 * Logger.info("Disprover: No Hypotheses"); } else {
		 * hypothesesString.delete(hypothesesString.length() - 2,
		 * hypothesesString.length());
		 * Logger.info("Disprover: Sending Hypotheses: " +
		 * UnicodeTranslator.toAscii(hypothesesString.toString())); }
		 */
		EventB goal = translateToEvalElement(sequent.goal());
		// Logger.info("Disprover: Sending Goal: "+
		// UnicodeTranslator.toAscii(predicateToProlog(goal)));

		AEventBContextParseUnit context = DisproverContextCreator
				.createDisproverContext(sequent, allHypotheses);

		// find the IEventBProject belonging to the sequent
		IPOSequent origin = (IPOSequent) sequent.getOrigin();
		IRodinProject project = origin.getRodinProject();
		IEventBProject evbProject = (IEventBProject) project
				.getAdapter(IEventBProject.class);
		ICounterExample counterExample = DisproverCommand.disprove(evbProject,
				allHypotheses, selectedHypotheses, goal, timeoutFactor,
				context, pm);
		// Logger.info("Disprover: Result: " + counterExample.toString());

		return counterExample;
	}

	private EventB translateToEvalElement(Predicate predicate) {
		String string = predicate.toStringFullyParenthesized();
		Set<IFormulaExtension> extensions = predicate.getFactory()
				.getExtensions();
		EventB eventB = new EventB(string, extensions);
		return eventB;
	}

	private String predicateToProlog(Predicate pred) {
		PrologTermStringOutput pto = new PrologTermStringOutput();
		TranslationVisitor v = new TranslationVisitor();
		pred.accept(v);
		ASTProlog p = new ASTProlog(pto, null);
		v.getPredicate().apply(p);
		return pto.toString();
	}

	/**
	 * Create a {@link IProofRule} containing the result from the disprover.
	 */
	private IReasonerOutput createDisproverResult(
			final ICounterExample counterExample, final IProverSequent sequent,
			final IReasonerInput input) {

		Predicate goal = sequent.goal();

		IAntecedent ante = ProverFactory.makeAntecedent(goal);

		if (counterExample == null) {
			return ProverFactory.reasonerFailure(this, input,
					"ProB: Error occurred.");
		}

		if (counterExample.timeoutOccured()) {
			System.out.println(sequent.toString() + ": Timeout occured.");
			return ProverFactory.reasonerFailure(this, input,
					"ProB: Timeout occurred.");
		}

		if (!counterExample.counterExampleFound() && counterExample.isProof()) {
			System.out.println(sequent.toString() + ": Proof.");
			return ProverFactory.makeProofRule(this, input, sequent.goal(),
					null, IConfidence.DISCHARGED_MAX,
					"ProB (no enumeration / all cases checked)");
		}

		if (!counterExample.counterExampleFound()) {
			System.out.println(sequent.toString() + ": Unsure.");

			return ProverFactory.reasonerFailure(
					this,
					input,
					"ProB: No Counter-Example found due to "
							+ counterExample.getReason()
							+ ", but there might exist one.");
		}

		if (counterExample.counterExampleFound()
				&& counterExample.onlySelectedHypotheses()) {
			System.out.println(sequent.toString()
					+ ": Counter-Example for selected hypotheses found.");

			return ProverFactory
					.reasonerFailure(
							this,
							input,
							"ProB: Counter-Example for selected Hypotheses found, Goal not provable from selected Hypotheses (may be provable with all Hypotheses)");
		}

		System.out.println(sequent.toString() + ": Counter-Example found.");
		return ProverFactory.makeProofRule(this, input, null, null,
				IConfidence.PENDING, counterExample.toString(), ante);
	}

	@Override
	public IReasonerInput deserializeInput(final IReasonerInputReader reader)
			throws SerializeException {
		return new DisproverReasonerInput();
	}

	@Override
	public void serializeInput(final IReasonerInput input,
			final IReasonerInputWriter writer) throws SerializeException {
	}

}
