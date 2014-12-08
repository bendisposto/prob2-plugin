package de.prob2.ui.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public class BrowserView extends ViewPart {

	private final int port;
	private Composite canvas;
	private Browser browser;
	protected String url;

	public BrowserView(String url) {
		this.url = url;
		port = WebConsole.getPort();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		createSWTBrowser(parent);
	}

	private void createSWTBrowser(Composite parent) {
		Browser b = new Browser(parent, SWT.NONE);
		this.browser = b;
		String u = getUrl();
		if (u != null && !u.isEmpty())
			load(u);
		canvas = b;
	}

//	public void refresh() {
//		browser.refresh();
//	}

	public void load(String url) {
		if (url == null || url.isEmpty()) {
			// FIXME log error?
			return;
		}
		if (url.startsWith("http://")) {
			browser.setUrl(url);
		} else {
			browser.setUrl("http://localhost:" + port + "/" + url);
		}
		VersionController.ensureInstalled();
	}

	protected String getUrl() {
		return url;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		canvas.setFocus();
	}

}