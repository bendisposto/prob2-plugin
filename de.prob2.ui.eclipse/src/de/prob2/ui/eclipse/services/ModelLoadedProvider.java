package de.prob2.ui.eclipse.services;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import de.prob.Main;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.StateSpace;

public class ModelLoadedProvider extends AbstractSourceProvider implements IModelChangedListener {

	public ModelLoadedProvider() {
		AnimationSelector selector = Main.getInjector().getInstance(AnimationSelector.class);
		selector.registerModelChangedListener(this);
	}
	
	public static final String SERVICE = "de.prob.ui.model_loaded";
	
	public static final String ENABLED = "enabled";
	public static final String DISABLED = "disabled";
	private boolean enabled = false;

	@Override
	public void dispose() {
	}

	@Override
	public Map<String,String> getCurrentState() {
		Map<String, String> currentState = new HashMap<String, String>(1);
		String current = enabled ? ENABLED : DISABLED;
		currentState.put(SERVICE, current);
		return currentState;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { SERVICE };
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(final boolean enabled) {
		if (nochange(enabled))
			return;
		this.enabled = enabled;
		fireSourceChanged(ISources.WORKBENCH, getCurrentState());
	}
	
	private boolean nochange(final boolean enabled) {
		return this.enabled == enabled;
	}

	@Override
	public void modelChanged(StateSpace s) {
		if(s == null) {
			setEnabled(false);
			return;
		}
		setEnabled(true);
	}

}
