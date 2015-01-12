package de.prob.ui.eventb;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import de.prob2.ui.eclipse.Activator;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BMotionLabelProvider implements ILabelProvider {

	private final Image bmsLogo;

	public BMotionLabelProvider() {
		ImageDescriptor imageDescriptor = Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/bms.png");
		this.bmsLogo = imageDescriptor.createImage();
	}

	public Image getImage(final Object element) {
		return this.bmsLogo;
	}

	public String getText(final Object element) {

		if (element instanceof BMotionStudioRodinFile)
			return ((BMotionStudioRodinFile) element).getName();
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
