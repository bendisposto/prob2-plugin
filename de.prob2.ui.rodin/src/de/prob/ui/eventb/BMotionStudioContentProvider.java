package de.prob.ui.eventb;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BMotionStudioContentProvider implements ITreeContentProvider {

	public Object[] getChildren(final Object parentElement) {

		List<Object> res = new ArrayList<Object>();

		if (parentElement instanceof IProject) {

			final IProject project = (IProject) parentElement;

			IFolder bmotionFolder = project.getFolder("bmotion");
			if (bmotionFolder.exists()) {

				try {
					IResource[] children = bmotionFolder.members();
					for (IResource rs : children) {
						if (rs.getFileExtension() != null
								&& rs.getFileExtension().equals("html")) {
							res.add(new BMotionStudioRodinFile(rs));
						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}

			}

		}

		return res.toArray(new BMotionStudioRodinFile[res.size()]);

	}

	public Object getParent(final Object element) {
		// do nothing
		return null;
	}

	public boolean hasChildren(final Object element) {
		return false;
	}

	public Object[] getElements(final Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		// do nothing

	}

	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
		// do nothing
	}

}
