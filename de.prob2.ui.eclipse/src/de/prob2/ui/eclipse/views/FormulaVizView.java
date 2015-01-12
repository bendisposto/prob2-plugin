package de.prob2.ui.eclipse.views;

import org.eclipse.ui.IMemento;

import de.prob2.ui.eclipse.BrowserView;

public class FormulaVizView extends BrowserView {

	public FormulaVizView() {
		super("sessions/FormulaView");
	}

	@Override
	public void saveState(final IMemento memento) {
		// We don't want to persist these views, so do nothing.
	}
}
