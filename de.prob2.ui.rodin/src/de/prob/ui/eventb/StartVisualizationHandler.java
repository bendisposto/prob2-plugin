package de.prob.ui.eventb;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob2.ui.eclipse.handlers.IFileView;
import de.prob2.ui.eclipse.views.BMSView;

/**
 * @author Lukas Ladenberger
 * 
 */
public class StartVisualizationHandler extends AbstractHandler implements
		IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection fSelection = HandlerUtil.getCurrentSelection(event);

		// Get the Selection
		if (fSelection instanceof IStructuredSelection) {

			IStructuredSelection ssel = (IStructuredSelection) fSelection;

			if (ssel.size() == 1) {

				Object selection = ssel.getFirstElement();

				if (selection instanceof BMotionStudioRodinFile) {

					IResource res = ((BMotionStudioRodinFile) selection)
							.getResource();

					IWorkbenchWindow window = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow();
					IWorkbenchPage activePage = window.getActivePage();

					try {
						String secId = UUID.randomUUID().toString();
						IViewPart view = activePage.showView(BMSView.ID, secId,
								IWorkbenchPage.VIEW_VISIBLE);
						if (view instanceof IFileView) {
							IFileView v = (IFileView) view;
							v.setFileName(res.getFullPath().toOSString());
						}

					} catch (PartInitException e) {
						e.printStackTrace();
					}

				}

			}

		}

		return null;

	}

}
