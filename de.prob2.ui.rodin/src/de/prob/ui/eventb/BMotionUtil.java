package de.prob.ui.eventb;

import java.io.File;
import java.util.UUID;

import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.prob2.ui.eclipse.handlers.IFileView;
import de.prob2.ui.eclipse.views.BMotionView;

public class BMotionUtil {

	public static void openBMotionView(BMotionStudioRodinProject project) {
		IResource res = project.getResource();
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		try {
			String secId = UUID.randomUUID().toString();
			IViewPart view = activePage.showView(BMotionView.ID, secId,
					IWorkbenchPage.VIEW_VISIBLE);
			if (view instanceof IFileView) {
				IFileView v = (IFileView) view;
				v.setFileName(res.getFullPath().toOSString() + File.separator
						+ "bmotion.json");
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}