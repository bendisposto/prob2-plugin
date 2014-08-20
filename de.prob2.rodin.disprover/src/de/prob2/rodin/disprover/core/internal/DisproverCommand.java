package de.prob2.rodin.disprover.core.internal;

import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.core.IEventBProject;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.osgi.service.prefs.Preferences;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.prob.Main;
import de.prob.animator.IAnimator;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.SetPreferenceCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.animator.domainobjects.EventB;
import de.prob.formula.TranslationVisitor;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob2.rodin.disprover.core.DisproverReasoner;
import de.prob2.rodin.disprover.core.job.ProBCommandJob;

/**
 * The DisproverCommand takes two sets of ASTs (one for the machine and a list
 * for the contexts) and tries to set them up with ProB. If setup is possible,
 * the arguments from that operation are joined with the provided variables and
 * returned as an {@link ICounterExample}.
 * <p>
 * 
 * This command is probably not useful without {@link DisproverReasoner}, which
 * calls it.
 * 
 * @author jastram
 */
public class DisproverCommand extends ComposedCommand {

	private static final String RESULT = "Result";

	private CounterExample counterExample;
	private final Set<EventB> allHypotheses;
	private final Set<EventB> selectedHypotheses;
	private final EventB goal;
	private final int timeout;

	private static ComposedCommand composed;

	public DisproverCommand(Set<EventB> allHypotheses2,
			Set<EventB> selectedHypotheses2, EventB goal2, int timeout) {
		this.allHypotheses = allHypotheses2;
		this.selectedHypotheses = selectedHypotheses2;
		this.goal = goal2;
		this.timeout = timeout;
	}

	public static ICounterExample disprove(IEventBProject project,
			Set<EventB> allHypotheses2, Set<EventB> selectedHypotheses2,
			EventB goal2, int timeout, AEventBContextParseUnit context,
			IProofMonitor pm) throws InterruptedException {

		Preferences prefNode = Platform.getPreferencesService().getRootNode()
				.node(InstanceScope.SCOPE).node("prob_disprover_preferences");

		// set clpfd and chr preference
		final SetPreferenceCommand setCLPFD = new SetPreferenceCommand("CLPFD",
				Boolean.toString(prefNode.getBoolean("clpfd", true)));
		final SetPreferenceCommand setCHR = new SetPreferenceCommand("CHR",
				Boolean.toString(prefNode.getBoolean("chr", true)));

		DisproverLoadCommand load = new DisproverLoadCommand(project, context);

		StartAnimationCommand start = new StartAnimationCommand();

		DisproverCommand disprove = new DisproverCommand(allHypotheses2,
				selectedHypotheses2, goal2, timeout
						* prefNode.getInt("timeout", 1000));

		composed = new ComposedCommand(setCLPFD, setCHR, load, start, disprove);

		final IAnimator animator = Main.getInjector().getInstance(
				IAnimator.class);

		final ProBCommandJob job = new ProBCommandJob(animator);
		job.setUser(true);
		job.schedule();

		animator.execute(composed);
		job.stopped = true;

		return disprove.getResult();

	}

	public ICounterExample getResult() {
		return counterExample;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("cbc_disprove");

		goal.printProlog(pto);

		pto.openList();
		for (EventB p : this.allHypotheses) {
			p.printProlog(pto);
		}
		pto.closeList();
		pto.openList();
		for (EventB p : this.selectedHypotheses) {
			p.printProlog(pto);
		}
		pto.closeList();
		pto.printNumber(timeout);
		pto.printVariable(RESULT);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {

		PrologTerm term = bindings.get(RESULT);

		counterExample = null;

		if ("time_out".equals(term.getFunctor())) {
			counterExample = new CounterExample(false, true, false);
		}
		if ("interrupted".equals(term.getFunctor())) {
			counterExample = new CounterExample(false, true, false);
		}
		if ("no_solution_found".equals(term.getFunctor())) {
			PrologTerm reason = term.getArgument(1);
			if (reason.hasFunctor("clpfd_overflow", 0)) {
				counterExample = new CounterExample(false, false,
						"CLPFD Integer Overflow");
			} else if (reason.hasFunctor("unfixed_deferred_sets", 0)) {
				counterExample = new CounterExample(false, false,
						"unfixed deferred sets in predicate");
			} else {
				counterExample = new CounterExample(false, false,
						reason.toString());
			}
		}

		if ("contradiction_found".equals(term.getFunctor())) {
			counterExample = new CounterExample(false, false, false);
			counterExample.setProof(true);
		}

		if ("solution".equals(term.getFunctor())) {
			counterExample = new CounterExample(true, false, false);
			ListPrologTerm vars = (ListPrologTerm) term.getArgument(1);

			for (PrologTerm e : vars) {
				counterExample.addVar(e.getArgument(1).getFunctor(), e
						.getArgument(3).getFunctor());
			}
		}

		if ("solution_on_selected_hypotheses".equals(term.getFunctor())) {
			counterExample = new CounterExample(true, false, true);
			ListPrologTerm vars = (ListPrologTerm) term.getArgument(1);

			for (PrologTerm e : vars) {
				counterExample.addVar(e.getArgument(1).getFunctor(), e
						.getArgument(3).getFunctor());
			}
		}

	}

}
