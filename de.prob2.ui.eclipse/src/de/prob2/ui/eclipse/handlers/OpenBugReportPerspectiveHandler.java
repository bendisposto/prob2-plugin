package de.prob2.ui.eclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class OpenBugReportPerspectiveHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		try {
			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
			workbench.showPerspective("de.prob2.bugreport",
					activeWorkbenchWindow);
		} catch (WorkbenchException e) {
			// ignore exceptions
		}
		return null;
	}

}
