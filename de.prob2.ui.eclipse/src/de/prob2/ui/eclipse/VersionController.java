package de.prob2.ui.eclipse;

import java.io.FileNotFoundException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.prob.cli.CliVersionNumber;
import de.prob.scripting.Api;
import de.prob.servlet.Main;

public class VersionController {

	private static volatile boolean checked = false;

	private void check() {
		if (!"false".equals(System.getProperty("checkVersion"))) {
			final Api api = Main.getInjector().getInstance(Api.class);
			final CliVersionNumber version = api.getVersion();

			if (version == null) {
				final Display display = Display.getDefault();
				final Shell shell = display.getActiveShell();
				final String dialogMessage = "You have no ProB binaries installed in your home directory."
						+ " Press \"Ok\" to download a compatible version.\n"
						+ "Make sure that you have a working internet connection.\n";
				final MessageDialog popup = new MessageDialog(shell,
						"Download ProB Binaries", null, dialogMessage,
						MessageDialog.WARNING, new String[] { "Ok", "Cancel" },
						0);
				final int result = popup.open();
				if (result == 0) {
					api.upgrade("2.0.0-snapshot");
				}
			} else if (!version.revision
					.equals("fbaa6d5c9323581aa64ba9e2526808ff27040922")) {
				final Display display = Display.getDefault();
				final Shell shell = display.getActiveShell();
				final String dialogMessage = "The ProB binary in your home directory may not be compatible with this version of the ProB 2.0 Plug-in."
						+ " Press \"Ok\" to download a compatible version.\n"
						+ "Make sure that you have a working internet connection.\n"
						+ "If you press \"Cancel\" we cannot guarantee that the plug-in will work correctly.";
				final MessageDialog popup = new MessageDialog(shell,
						"Download ProB Binaries", null, dialogMessage,
						MessageDialog.WARNING, new String[] { "Ok", "Cancel" },
						0);
				final int result = popup.open();
				if (result == 0) {
					try {
						api.upgrade("2.0.0-snapshot");
					} catch (final Exception e) {
						if (e instanceof FileNotFoundException) {
							final MessageDialog popup2 = new MessageDialog(
									shell,
									"Download ProB Binaries",
									null,
									"The download failed because your ProB binary is in use."
											+ " We will try to load your machine with your current binary.",
											MessageDialog.ERROR, new String[] { "Ok" },
											0);
							popup2.open();
						}
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

		final VersionController c = new VersionController();
		c.check();

	}

}
