package de.prob.ui.eventb;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
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
						if (rs instanceof IContainer) {
							IContainer pfolder = (IContainer) rs;
							List<BMotionStudioRodinFile> files = new ArrayList<BMotionStudioRodinFile>();
							for (IResource rs2 : pfolder.members()) {
								files.add(new BMotionStudioRodinFile(rs2));
							}
							res.add(new BMotionStudioRodinProject(pfolder,
									files));
						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}

			}

		} else if (parentElement instanceof BMotionStudioRodinProject) {
			res.addAll(((BMotionStudioRodinProject) parentElement).getFiles());
		}

		return res.toArray(new Object[res.size()]);

	}

	public Object getParent(final Object element) {
		// do nothing
		return null;
	}

	public boolean hasChildren(final Object element) {
		return element instanceof BMotionStudioRodinProject;
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
