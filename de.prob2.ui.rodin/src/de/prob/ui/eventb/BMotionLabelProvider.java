package de.prob.ui.eventb;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.prob2.ui.eclipse.Activator;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BMotionLabelProvider implements ILabelProvider {

	private final Image bmsLogo;

	private final Image fileIcon;

	public BMotionLabelProvider() {
		ImageDescriptor imageDescriptor = Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/bms.png");
		this.bmsLogo = imageDescriptor.createImage();
		this.fileIcon = PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_FILE).createImage();
	}

	public Image getImage(final Object element) {
		if (element instanceof BMotionStudioRodinProject) {
			return this.bmsLogo;
		}
		return fileIcon;
	}

	public String getText(final Object element) {

		if (element instanceof BMotionStudioRodinFile) {
			return ((BMotionStudioRodinFile) element).getResource().getName();
		} else if (element instanceof BMotionStudioRodinProject) {
			return ((BMotionStudioRodinProject) element).getResource()
					.getName();
		}

		return element.toString();

	}

	public void addListener(final ILabelProviderListener listener) {

	}

	public void dispose() {

	}

	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	public void removeListener(final ILabelProviderListener listener) {

	}

}
