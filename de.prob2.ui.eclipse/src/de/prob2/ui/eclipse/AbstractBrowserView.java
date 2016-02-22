package de.prob2.ui.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public abstract class AbstractBrowserView extends ViewPart {
	protected final int port;
	protected Composite canvas;
	protected Browser browser;
	protected String url;
	protected boolean requiresProB;

	public AbstractBrowserView(final String url) {
		this.url = url;
		requiresProB = false;
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

	private void createSWTBrowser(final Composite parent) {
		final Browser b = new Browser(parent, SWT.NONE);
		browser = b;

		final String u = getUrl();
		if (u != null && !u.isEmpty()) {
			load(u);
		}
		canvas = b;
	}

	// public void refresh() {
	// browser.refresh();
	// }

	public abstract void load(final String url);

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
