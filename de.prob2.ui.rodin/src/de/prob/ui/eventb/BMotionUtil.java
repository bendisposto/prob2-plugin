package de.prob.ui.eventb;

import java.util.UUID;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.prob2.ui.eclipse.handlers.IFileView;
import de.prob2.ui.eclipse.views.BMSView;

public class BMotionUtil {	
	
	public static void openBMotionView(ISelection selection) {
		
		// Get the Selection
		if (selection instanceof IStructuredSelection) {

			IStructuredSelection ssel = (IStructuredSelection) selection;

			if (ssel.size() == 1) {

				Object obj = ssel.getFirstElement();

				if (obj instanceof BMotionStudioRodinFile) {

					IResource res = ((BMotionStudioRodinFile) obj)
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
		
	}

}
