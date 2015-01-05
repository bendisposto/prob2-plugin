package de.prob2.ui.eclipse;

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
				.equals("244cf93baecc5e982793931baa704855200deea2")) {
			api.upgrade("milestone-21");
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
