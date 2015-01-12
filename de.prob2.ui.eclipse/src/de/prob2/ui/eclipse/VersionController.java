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
						.equals("50c91ce90de1d72fed61ac916548e2a4fc28b992")) {
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
				api.upgrade("advance-2015-Jan");
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
