package de.prob2.ui.eclipse;

import java.io.FileNotFoundException;

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

		if (version == null) {
			Display display = Display.getDefault();
			Shell shell = display.getActiveShell();
			String dialogMessage = "You have no ProB binaries installed in your home directory."
					+ " Press \"Ok\" to download a compatible version.\n"
					+ "Make sure that you have a working internet connection.\n";
			MessageDialog popup = new MessageDialog(shell,
					"Download ProB Binaries", null, dialogMessage,
					MessageDialog.WARNING, new String[] { "Ok", "Cancel" }, 0);
			int result = popup.open();
			if (result == 0) {
				api.upgrade("milestone-23");
			}
		} else if (!version.revision
				.equals("7a4086e389c8d30ef537d877f046817bfadc32ab")) {
			Display display = Display.getDefault();
			Shell shell = display.getActiveShell();
			String dialogMessage = "The ProB binary in your home directory may not be compatible with this version of the ProB 2.0 Plug-in."
					+ " Press \"Ok\" to download a compatible version.\n"
					+ "Make sure that you have a working internet connection.\n"
					+ "If you press \"Cancel\" we cannot guarantee that the plug-in will work correctly.";
			MessageDialog popup = new MessageDialog(shell,
					"Download ProB Binaries", null, dialogMessage,
					MessageDialog.WARNING, new String[] { "Ok", "Cancel" }, 0);
			int result = popup.open();
			if (result == 0) {
				try {
					api.upgrade("milestone-23");
				} catch (Exception e) {
					if (e instanceof FileNotFoundException) {
						MessageDialog popup2 = new MessageDialog(
								shell,
								"Download ProB Binaries",
								null,
								"The download failed because your ProB binary is in use."
										+ " We will try to load your machine with your current binary.",
										MessageDialog.ERROR, new String[] { "Ok" }, 0);
						popup2.open();
					}
				}
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
