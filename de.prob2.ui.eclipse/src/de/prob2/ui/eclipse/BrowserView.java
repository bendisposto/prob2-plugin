package de.prob2.ui.eclipse;

import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;

public class BrowserView extends AbstractBrowserView {

	public BrowserView(final String url) {
		super(url);
	}

	@Override
	public void load(final String url) {
		if (url == null || url.isEmpty()) {
			// FIXME log error?
			return;
		}

		browser.addProgressListener(new ProgressAdapter() {
			@Override
			public void completed(final ProgressEvent event) {
				browser.removeProgressListener(this);
				browser.refresh(); // <----
			}
		});
		if (url.startsWith("http://")) {
			browser.setUrl(url);
		} else {
			String theUrl = "http://localhost:" + port + "/" + url;
			System.out.println("Loading: ###" + theUrl + "###");
			browser.setUrl(theUrl);
		}
		VersionController.ensureInstalled();
	}

}