/**
 * 
 */
package de.prob2.rodin.disprover.core.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.prob.animator.IAnimator;

public class ProBCommandJob extends Job {

	public volatile boolean stopped = false;
	public volatile boolean interrupt = false;
	private IAnimator a;

	public ProBCommandJob(final IAnimator a) {
		super("Disprove");
		this.a = a;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		while (!stopped) {
			// sleep
		}
		return interrupt ? Status.CANCEL_STATUS : Status.OK_STATUS;
	}

	@Override
	protected void canceling() {
		interrupt = true;
		a.sendInterrupt();
	}

}
