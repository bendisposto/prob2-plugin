package de.prob.ui.eventb;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BMotionStudioActionProvider extends CommonActionProvider {

	public static String GROUP_FILEACTIONS = "fileactionsGroup";

	ICommonActionExtensionSite site;

	@Override
	public void init(final ICommonActionExtensionSite aSite) {
		super.init(aSite);
		site = aSite;
	}

	@Override
	public void fillActionBars(final IActionBars actionBars) {
		super.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
				getOpenAction(site));
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, getOpenAction(site));
	}

	/**
	 * Provides an open action for BMotion Studio files
	 * 
	 * @param site
	 * @return An open action
	 */
	private static Action getOpenAction(final ICommonActionExtensionSite site) {
		return new Action("Open") {
			@Override
			public void run() {
				BMotionUtil.openBMotionView(site.getStructuredViewer()
						.getSelection());
			}
		};
	}

}
