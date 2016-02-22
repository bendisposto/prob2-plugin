package de.prob2.ui.eclipse.handlers;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenConsoleHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final String id = event.getParameter("de.prob2.ui.eclipse.views.id");
		final String selected = "";

		final IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		final IWorkbenchPage activePage = window.getActivePage();

		try {
			final String secId = UUID.randomUUID().toString();
			final IViewPart view = activePage.showView(id, secId,
					IWorkbenchPage.VIEW_ACTIVATE);
			if (view instanceof IFileView) {
				final IFileView v = (IFileView) view;
				v.setFileName(selected);
			}

		} catch (final PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

}
