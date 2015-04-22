package de.prob.ui.eventb;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BMotionStudioRodinFile extends AbstractBMotionStudioRodinFile {

	private List<BMotionStudioRodinFile> files;

	public BMotionStudioRodinFile(IResource resource) {
		super(resource);
		this.files = new ArrayList<BMotionStudioRodinFile>();
	}

	public List<BMotionStudioRodinFile> getFiles() {
		return files;
	}

}
