package de.prob2.ui.eclipse.views;

import java.util.UUID;

import de.prob.Main;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.visualization.VisualizationException;
import de.prob.webconsole.servlets.visualizations.IRefreshListener;
import de.prob.webconsole.servlets.visualizations.StateSpaceServlet;
import de.prob.webconsole.servlets.visualizations.StateSpaceSession;
import de.prob2.ui.eclipse.BrowserView;

public class StateSpaceVizView extends BrowserView implements IRefreshListener {

	private final StateSpaceServlet servlet;

	public StateSpaceVizView() {
		super("");
		servlet = Main.getInjector().getInstance(StateSpaceServlet.class);
		String sessionId = UUID.randomUUID().toString();
		try {
			servlet.openSession(sessionId);
		} catch (AnimationNotLoadedException e) {
			e.printStackTrace();
		} catch (VisualizationException e) {
			e.printStackTrace();
		}
		this.url = "statespace_servlet/?init=" + sessionId;
		StateSpaceSession session = servlet.getSessionServlet(sessionId);
		session.registerRefreshListener(this);
	}

}
