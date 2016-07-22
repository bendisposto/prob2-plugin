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

			try {
				final IContainer project = (IContainer) parentElement;
				IResource[] children = project.members();
				for (IResource rs : children) {

					if (rs instanceof IContainer) {
						IContainer pfolder = (IContainer) rs;
						if (pfolder.findMember("bmotion.json") != null) {
							List<BMotionStudioRodinFile> files = new ArrayList<BMotionStudioRodinFile>();
							for (IResource fs : pfolder.members()) {
								files.add(new BMotionStudioRodinFile(fs));
							}
							res.add(new BMotionStudioRodinProject(pfolder,
									files));
						}
					}

				}
			} catch (CoreException e) {
				e.printStackTrace();
			}

		} else if (parentElement instanceof BMotionStudioRodinProject) {
			res.addAll(((BMotionStudioRodinProject) parentElement).getFiles());
		} else if (parentElement instanceof BMotionStudioRodinFile) {

			BMotionStudioRodinFile p = (BMotionStudioRodinFile) parentElement;

			try {
				IResource r = p.getResource();
				if (r instanceof IFolder) {
					if (r instanceof IContainer) {
						IContainer pfolder = (IContainer) r;
						for (IResource fs : pfolder.members()) {
							p.getFiles().add(new BMotionStudioRodinFile(fs));
						}
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}

			res.addAll(((BMotionStudioRodinFile) parentElement).getFiles());

		}

		return res.toArray(new Object[res.size()]);

	}

	public Object getParent(final Object element) {
		// do nothing
		return null;
	}

	public boolean hasChildren(final Object element) {
		if (element instanceof BMotionStudioRodinProject) {
			return true;
		} else if (element instanceof BMotionStudioRodinFile) {
			BMotionStudioRodinFile rf = (BMotionStudioRodinFile) element;
			return rf.getResource() instanceof IContainer;
		}
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