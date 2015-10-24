package de.prob.ui.eventb;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

import com.google.inject.Injector;

import de.prob.Main;
import de.prob.exception.ProBError;
import de.prob.model.eventb.EventBModel;
import de.prob.scripting.EventBFactory;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob2.ui.eclipse.ErrorHandler;
import de.prob2.ui.eclipse.VersionController;

public class StartAnimationHandler extends AbstractHandler {

	private ISelection fSelection;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		VersionController.ensureInstalled();

		fSelection = HandlerUtil.getCurrentSelection(event);

		final IEventBRoot rootElement = getRootElement();

		String fileName = rootElement.getResource().getRawLocation()
				.makeAbsolute().toOSString();
		if (fileName.endsWith(".buc")) {
			fileName = fileName.replace(".buc", ".bcc");
		} else {
			fileName = fileName.replace(".bum", ".bcm");
		}

		Injector injector = Main.getInjector();

		final EventBFactory instance = injector
				.getInstance(EventBFactory.class);

		EventBModel model;
		try {

			StateSpace s = instance.loadModelFromEventBFile(fileName,
					new HashMap<String, String>());
			model = (EventBModel) s.getModel();

			Trace t = new Trace(s);
			AnimationSelector selector = injector
					.getInstance(AnimationSelector.class);
			selector.clearUnprotected();
			selector.addNewAnimation(t, false);

			System.gc();

			final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();

			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					// switch perspective
					try {
						workbenchWindow.getWorkbench().showPerspective(
								"de.prob2.perspective", workbenchWindow);
					} catch (WorkbenchException e) {

					}
				}
			});
		} catch (IOException e1) {
			ErrorHandler
					.errorMessage("Loading of the model failed."
							+ " Please check to make sure that the Rodin static checker has "
							+ "produced a valid static checked file (.bcc or .bcm)."
							+ " If not, try cleaning the project.");
		} catch (ProBError e) {
			ErrorHandler.errorMessage("ProB was not able to load the model.\n"
					+ "This is because: " + e.getMessage());
		}

		return null;
	}

	private IEventBRoot getRootElement() {
		IEventBRoot root = null;
		if (fSelection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) fSelection;
			if (ssel.size() == 1) {
				final Object element = ssel.getFirstElement();
				if (element instanceof IEventBRoot) {
					root = (IEventBRoot) element;
				} else if (element instanceof IFile) {
					IRodinFile rodinFile = RodinCore.valueOf((IFile) element);
					if (rodinFile != null) {
						root = (IEventBRoot) rodinFile.getRoot();
					}
				}
			}
		}
		return root;
	}

}
