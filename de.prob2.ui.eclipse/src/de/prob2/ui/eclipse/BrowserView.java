package de.prob2.ui.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;
import de.prob.webconsole.servlets.visualizations.IRefreshListener;

public class BrowserView extends ViewPart implements IRefreshListener {

	private final int port;
	private Composite canvas;
	private Object browser;
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

		String force = System.getProperty("enforceJavaFX");
		createSWTBrowser(parent);
	}

	private void createSWTBrowser(Composite parent) {
		Browser b = new Browser(parent, SWT.NONE);
		this.browser = b;
		load(getUrl());
		canvas = b;
	}

	public void refresh() {
		if (browser instanceof Browser) {
			((Browser) browser).refresh();
			return;
		}
		if (browser instanceof javafx.scene.web.WebEngine) {
			((javafx.scene.web.WebEngine) browser).reload();
		}
	}

	public void load(String url) {
		if (url != null) {
			if (browser instanceof Browser) {
				((Browser) browser).setUrl("http://localhost:" + port + "/"
						+ url);
				return;
			} else if (browser instanceof javafx.scene.web.WebEngine) {
				((javafx.scene.web.WebEngine) browser).load("http://localhost:"
						+ port + "/" + url);
			}
		}
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