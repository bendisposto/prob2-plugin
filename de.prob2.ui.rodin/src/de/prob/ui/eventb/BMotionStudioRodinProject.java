package de.prob.ui.eventb;

import java.util.List;

import org.eclipse.core.resources.IContainer;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BMotionStudioRodinProject extends AbstractBMotionStudioRodinFile {

	private List<BMotionStudioRodinFile> files;

	public BMotionStudioRodinProject(IContainer resource,
			List<BMotionStudioRodinFile> files) {
		super(resource);
		this.files = files;
	}

	public List<BMotionStudioRodinFile> getFiles() {
		return files;
	}

}
