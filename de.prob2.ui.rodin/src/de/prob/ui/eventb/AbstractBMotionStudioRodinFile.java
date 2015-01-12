package de.prob.ui.eventb;

import org.eclipse.core.resources.IResource;

public class AbstractBMotionStudioRodinFile {

	private IResource resource;

	public AbstractBMotionStudioRodinFile(IResource resource) {
		this.resource = resource;
	}

	public IResource getResource() {
		return resource;
	}
	
}
