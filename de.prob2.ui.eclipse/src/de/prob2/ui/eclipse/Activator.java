package de.prob2.ui.eclipse;

import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import de.bmotion.core.BMotionServer;
import de.bmotion.prob.ProBServerFactory;
import de.prob.servlet.Main;
import de.prob.webconsole.WebConsole;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static volatile boolean ready = false;

	public static BMotionServer bmotionServer;

	public static void runProB(final String... args) {
		Main.restricted = false;
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					WebConsole.run("127.0.0.1", new Runnable() {
						@Override
						public void run() {
							Activator.ready = true;
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(r).start();

		// Start BMotionWeb for ProB
		new Thread(new Runnable() {

			@Override
			public void run() {

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				String[] bmsArgs = { "-local", "-workspace", workspace.getRoot().getLocation().toOSString() };
				try {
					bmotionServer = ProBServerFactory.getServer(bmsArgs);
					bmotionServer.setMode(BMotionServer.MODE_INTEGRATED);
					Bundle bundle = Platform.getBundle("de.prob2.ui.bmotion");
					URL url = FileLocator.find(bundle, new Path("resources"), null);			
					bmotionServer.addResourcePath(FileLocator.toFileURL(url));
					bmotionServer.startWithJetty();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();

	}

	// The plug-in ID
	public static final String PLUGIN_ID = "de.prob2.ui.eclipse"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		runProB("-s", "-local");
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
