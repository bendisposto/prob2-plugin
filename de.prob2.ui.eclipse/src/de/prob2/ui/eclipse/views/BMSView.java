package de.prob2.ui.eclipse.views;

import de.prob2.ui.eclipse.BrowserView;
import de.prob2.ui.eclipse.handlers.IFileView;

public class BMSView extends BrowserView implements IFileView {

	private String filename;

	public BMSView() {
		super(null);
	}

	@Override
	public void setFileName(String fn) {
		this.filename = fn;
		load("bms/?template=" + filename + "&tool=BAnimation");
	}
}
