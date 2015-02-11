package de.prob2.ui.eclipse;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ErrorHandler {

	public static void errorMessage(final String message) {
		Display display = Display.getDefault();
		Shell shell = display.getActiveShell();
		MessageDialog popup = new MessageDialog(shell, "Error", null, message,
				MessageDialog.ERROR, new String[] { "Ok" }, 0);
		popup.open();
	}
}
