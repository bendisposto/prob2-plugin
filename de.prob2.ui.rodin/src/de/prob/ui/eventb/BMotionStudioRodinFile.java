package de.prob.ui.eventb;

import org.eclipse.core.resources.IResource;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BMotionStudioRodinFile {

	private IResource resource;
	
	private String name;
	
	public BMotionStudioRodinFile(IResource resource, String name) {
		this.resource = resource;
		this.name = name;
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
