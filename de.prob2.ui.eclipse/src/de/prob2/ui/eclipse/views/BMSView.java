package de.prob2.ui.eclipse.views;

import org.eclipse.ui.IMemento;

import de.prob2.ui.eclipse.AbstractBrowserView;
import de.prob2.ui.eclipse.Activator;
import de.prob2.ui.eclipse.handlers.IFileView;

public class BMSView extends AbstractBrowserView implements IFileView {

	public static String ID = "de.prob2.ui.views.bms";

	private final int bmotionPort;

	public BMSView() {
		super(null);
		bmotionPort = Activator.bmotionServer.getPort();
	}

	@Override
	public void setFileName(final String fn) {
		if (fn != null) {
			load("http://localhost:" + bmotionPort + "/bms" + fn);
		}
	}

	@Override
	public void load(final String url) {
		if (url == null || url.isEmpty()) {
			// FIXME log error?
			return;
		}

		if (url.startsWith("http://")) {
			browser.setUrl(url);
		} else {
			String theUrl = "http://localhost:" + port + "/" + url;
			System.out.println("Loading: ###" + theUrl + "###");
			browser.setUrl(theUrl);
		}

	}

	@Override
	public void saveState(final IMemento memento) {
		// TODO Auto-generated method stub
	}

}
