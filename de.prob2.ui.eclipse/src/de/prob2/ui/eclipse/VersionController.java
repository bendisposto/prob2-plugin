package de.prob2.ui.eclipse;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.prob.Main;
import de.prob.cli.CliVersionNumber;
import de.prob.scripting.Api;

public class VersionController {

	private static volatile boolean checked = false;

	private void check() {
		Api api = Main.getInjector().getInstance(Api.class);
		CliVersionNumber version = api.getVersion();

		if (version == null
				|| !version.revision
						.equals("92c044e3dfcc8d07a758b342a3c63d2374706158")) {
			Display display = Display.getDefault();
			Shell shell = display.getActiveShell();
			String dialogMessage = "You either have no ProB binaries installed in your home directory, or your binaries are incompatible."
					+ " Press \"Ok\" to download a compatible version.\n"
					+ "Make sure that you have a working internet connection.\n"
					+ "If you press \"Cancel\" we do not guarantee that ProB will work.";
			MessageDialog popup = new MessageDialog(shell,
					"Download ProB Binaries", null, dialogMessage,
					MessageDialog.WARNING, new String[] { "Ok", "Cancel" }, 0);
			int result = popup.open();
			if (result == 0) {
				api.upgrade("milestone-23");
			}
		}
	}

	public static synchronized void ensureInstalled() {
		if (checked) {
			return;
		}
		checked = true;

		VersionController c = new VersionController();
		c.check();

	}

}
