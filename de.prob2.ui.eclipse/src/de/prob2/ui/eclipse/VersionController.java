package de.prob2.ui.eclipse;

import com.google.inject.Injector;

import de.prob.Main;
import de.prob.cli.CliVersionNumber;
import de.prob.scripting.Api;

public class VersionController {

	private final Injector injector;
	private static volatile boolean checked = false;

	public VersionController() {
		injector = Main.getInjector();
	}

	private void check() {
		Api api = injector.getInstance(Api.class);
		CliVersionNumber version = api.getVersion();
		if (version == null || !version.revision.equals("299d7b6fe367278d41a6d515e76e42b4253b7a47")) {
			api.upgrade("milestone-20");
		}
	}

	public static synchronized void ensureInstalled() {
		if (checked)
			return;
		checked = true;

		VersionController c = new VersionController();
		c.check();

	}

}
