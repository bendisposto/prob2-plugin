package de.prob2.ui.eclipse.handlers;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenViewHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String id = event.getParameter("de.prob2.ui.eclipse.views.id");

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();

		try {
			String secId = UUID.randomUUID().toString();
			activePage.showView(id, secId, IWorkbenchPage.VIEW_VISIBLE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}
}
