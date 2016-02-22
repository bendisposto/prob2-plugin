package de.prob.ui.eventb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

import com.google.inject.Injector;

import de.prob.exception.ProBError;
import de.prob.model.eventb.EventBModel;
import de.prob.scripting.EventBFactory;
import de.prob.scripting.ExtractedModel;
import de.prob.scripting.LoadClosures;
import de.prob.scripting.ModelTranslationError;
import de.prob.servlet.Main;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob2.ui.eclipse.ErrorHandler;

public class StartAnimationHandler extends AbstractHandler {

	private static final String PROB_ANIMATION_PREFERENCES = "prob_animation_preferences";
	private ISelection fSelection;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		fSelection = HandlerUtil.getCurrentSelection(event);

		final IEventBRoot rootElement = getRootElement();

		String fileName = rootElement.getResource().getRawLocation()
				.makeAbsolute().toOSString();
		if (fileName.endsWith(".buc")) {
			fileName = fileName.replace(".buc", ".bcc");
		} else {
			fileName = fileName.replace(".bum", ".bcm");
		}

		final Injector injector = Main.getInjector();

		final EventBFactory instance = injector
				.getInstance(EventBFactory.class);

		try {
			final ExtractedModel<EventBModel> em = instance.extract(fileName);

			final Map<String, String> prefs = getPreferences();
			final StateSpace s = em.load(prefs);
			LoadClosures.getEVENTB().call(s);

			final Trace t = new Trace(s);
			final AnimationSelector selector = injector
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
					} catch (final WorkbenchException e) {

					}
				}
			});
		} catch (final IOException e1) {
			ErrorHandler
			.errorMessage("Loading of the model failed."
					+ " Please check to make sure that the Rodin static checker has "
					+ "produced a valid static checked file (.bcc or .bcm)."
					+ " If not, try cleaning the project.");
		} catch (final ProBError e) {
			ErrorHandler.errorMessage("ProB was not able to load the model.\n"
					+ "This is because: " + e.getMessage());
		} catch (final ModelTranslationError e) {
			ErrorHandler.errorMessage("Translating the model into a format that ProB understands"
					+ " was not successful. This is because: " + e.getMessage());
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
					final IRodinFile rodinFile = RodinCore.valueOf((IFile) element);
					if (rodinFile != null) {
						root = (IEventBRoot) rodinFile.getRoot();
					}
				}
			}
		}
		return root;
	}

	private Map<String, String> getPreferences() {
		final Preferences node = Platform.getPreferencesService().getRootNode()
				.node(InstanceScope.SCOPE).node(PROB_ANIMATION_PREFERENCES);
		String[] names;
		try {
			names = node.keys();
		} catch (final BackingStoreException e) {
			names = new String[0];
			ErrorHandler.errorMessage("Error while storing ProB Preferences"+e.getMessage());
		}
		final Map<String, String> prefs = new HashMap<String, String>();
		for (final String string : names) {
			prefs.put(string, node.get(string, null));
		}

		return prefs;

	}

}
