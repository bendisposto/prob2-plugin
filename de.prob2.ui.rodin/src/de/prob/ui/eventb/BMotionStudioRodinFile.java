package de.prob.ui.eventb;

import org.eclipse.core.resources.IResource;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BMotionStudioRodinFile {

	private IResource resource;
	
	public BMotionStudioRodinFile(IResource resource) {
		this.resource = resource;
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

}
