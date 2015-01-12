package de.prob.ui.eventb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.ide.IDE;
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
		menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT,
				getDeleteAction(site));
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH,
				buildOpenWithMenu());
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

				ISelection selection = site.getStructuredViewer()
						.getSelection();

				// Get the Selection
				if (selection instanceof IStructuredSelection) {

					IStructuredSelection ssel = (IStructuredSelection) selection;

					if (ssel.size() == 1) {

						Object obj = ssel.getFirstElement();

						if (obj instanceof BMotionStudioRodinProject) {
							BMotionUtil
									.openBMotionView((BMotionStudioRodinProject) obj);
						} else if (obj instanceof BMotionStudioRodinFile) {
							BMotionStudioRodinFile bmsFile = (BMotionStudioRodinFile) obj;
							IWorkbenchPage page = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getActivePage();
							try {
								IDE.openEditor(page,
										(IFile) bmsFile.getResource());
							} catch (PartInitException e) {
								e.printStackTrace();
							}

						}

					}

				}

			}
		};
	}

	private static Action getDeleteAction(final ICommonActionExtensionSite site) {
		Action deleteAction = new Action() {
			@Override
			public void run() {
				if (!(site.getStructuredViewer().getSelection().isEmpty())) {

					Collection<BMotionStudioRodinProject> set = new ArrayList<BMotionStudioRodinProject>();

					IStructuredSelection ssel = (IStructuredSelection) site
							.getStructuredViewer().getSelection();

					for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
						final Object obj = it.next();
						if (!(obj instanceof BMotionStudioRodinProject)) {
							continue;
						}
						BMotionStudioRodinProject elem = (BMotionStudioRodinProject) obj;
						set.add(elem);
					}

					int answer = YesToAllMessageDialog.YES;
					for (BMotionStudioRodinProject element : set) {

						if (answer != YesToAllMessageDialog.YES_TO_ALL) {
							answer = YesToAllMessageDialog
									.openYesNoToAllQuestion(
											site.getViewSite().getShell(),
											"Confirm File Delete",
											"Are you sure you want to delete '"
													+ ((BMotionStudioRodinProject) element)
															.getResource()
															.getName()
													+ "' in project '"
													+ element.getResource()
															.getProject()
															.getName() + "' ?");
						}

						if (answer == YesToAllMessageDialog.NO_TO_ALL
								|| answer == YesToAllMessageDialog.CANCEL)
							break;

						if (answer != YesToAllMessageDialog.NO) {
							try {
								((BMotionStudioRodinProject) element)
										.getResource().delete(true,
												new NullProgressMonitor());
							} catch (PartInitException e) {
								MessageDialog.openError(null, "Error",
										"Could not delete file");
							} catch (CoreException e) {
								e.printStackTrace();
							}
						}

					}

				}
			}
		};
		deleteAction.setText("&Delete");
		deleteAction.setToolTipText("Delete these elements");
		return deleteAction;
	}

	MenuManager buildOpenWithMenu() {
		MenuManager menu = new MenuManager("Open With",
				ICommonMenuConstants.GROUP_OPEN_WITH);
		final StructuredViewer viewer = getActionSite().getStructuredViewer();
		ISelection selection = viewer.getSelection();
		// Get the Selection
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if (obj instanceof BMotionStudioRodinFile) {
					menu.add(new OpenWithMenu(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage(),
							((BMotionStudioRodinFile) obj).getResource()));
				}
			}
		}
		return menu;
	}

}
