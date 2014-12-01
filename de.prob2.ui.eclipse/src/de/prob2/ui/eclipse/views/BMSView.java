package de.prob2.ui.eclipse.views;

import de.prob2.ui.eclipse.Activator;
import de.prob2.ui.eclipse.BrowserView;
import de.prob2.ui.eclipse.handlers.IFileView;

public class BMSView extends BrowserView implements IFileView {

	public static String ID = "de.prob2.ui.views.bms";

	private final int bmotionPort;

	public BMSView() {
		super(null);
		bmotionPort = Activator.bmotionServer.getPort();
	}

	@Override
	public void setFileName(String fn) {
		if (fn != null) {
			load("http://localhost:" + bmotionPort + "/bms" + fn);
		}
	}

}
