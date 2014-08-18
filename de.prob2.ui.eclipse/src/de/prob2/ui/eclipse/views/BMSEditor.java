package de.prob2.ui.eclipse.views;

import de.prob2.ui.eclipse.BrowserView;
import de.prob2.ui.eclipse.handlers.IFileView;

public class BMSEditor extends BrowserView implements IFileView {

	private String filename;

	public BMSEditor() {
		super(null);
	}

	@Override
	public void setFileName(String fn) {
		this.filename = fn;
		load("bmseditor/?template=" + filename + "&tool=BAnimation");
	}

}
