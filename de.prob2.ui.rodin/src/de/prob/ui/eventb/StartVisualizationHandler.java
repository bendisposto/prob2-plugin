package de.prob.ui.eventb;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Lukas Ladenberger
 * 
 */
public class StartVisualizationHandler extends AbstractHandler implements
		IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		BMotionUtil.openBMotionView(HandlerUtil.getCurrentSelection(event));
		return null;
	}

}
