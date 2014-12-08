package de.prob2.ui.eclipse.handlers;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenViewHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		String id = event.getParameter("de.prob2.ui.eclipse.views.id");
		String selected = "";


		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();

		try {
			String secId = UUID.randomUUID().toString();
			IViewPart view = activePage.showView(id, secId,
					IWorkbenchPage.VIEW_VISIBLE);
			if (view instanceof IFileView) {
				IFileView v = (IFileView) view;
				v.setFileName(selected);
			}

		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

}
