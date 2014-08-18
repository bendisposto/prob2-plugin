package de.prob2.ui.eclipse;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		final String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		// LEFT ---------------------------------
		// Place the project explorer to left of editor area.
		final IFolderLayout left = layout.createFolder("left",
				IPageLayout.LEFT, 0.15f, editorArea);
		left.addView("de.prob2.ui.eclipse.events");

		final IFolderLayout leftb = layout.createFolder("leftb",
				IPageLayout.BOTTOM, 0.65f, "left");
		leftb.addView("fr.systerel.explorer.navigator.view");
		leftb.addView("org.eventb.ui.views.RodinProblemView");
		// ---------------------------------

		// MAIN ---------------------------------
		// Properties view + observer view + control panel
		IFolderLayout bottom1 = layout.createFolder("bottom1",
				IPageLayout.BOTTOM, 0.65f, editorArea);
		bottom1.addView("de.prob2.ui.eclipse.stateinspector");
		bottom1.addView("de.prob2.ui.eclipse.animations");
		bottom1.addView("de.prob2.ui.eclipse.modelchecking");
		bottom1.addView(IPageLayout.ID_PROP_SHEET);
		// bottom1.addView("de.prob.ui.EventErrorView");

		// Place the outline to right of editor area.
		final IFolderLayout main1 = layout.createFolder("main1",
				IPageLayout.BOTTOM, 0.5f, editorArea);
		main1.addView("de.prob2.ui.eclipse.currenttrace");
		// right1.addView("de.prob.ui.ltl.CounterExampleView");
		// Placeholder for new visualization views

		// ---------------------------------
	}

}
