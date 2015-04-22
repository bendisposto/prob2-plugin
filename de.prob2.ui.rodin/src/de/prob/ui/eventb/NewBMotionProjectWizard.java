package de.prob.ui.eventb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.rodinp.core.RodinCore;

public class NewBMotionProjectWizard extends Wizard implements INewWizard {

	// The wizard page.
	private NewBMotionProjectWizardPage page;

	// The selection when the wizard is launched.
	private IStructuredSelection selection;

	public NewBMotionProjectWizard() {
		super();
	}

	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection sel) {
		this.selection = sel;
	}

	@Override
	public boolean performFinish() {

		// New project/file name
		final String projectName = page.getFileName();

		final String machineFileName = page.getMachineName();

		// Selected rodin project root
		final String projectRoot = page.getProjectRoot();

		final IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(final IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(projectRoot, projectName, page.getProject(),
							machineFileName, monitor);
				} catch (final CoreException e) {
					Logger.getAnonymousLogger().log(Level.SEVERE,
							"CoreException", e);
				} finally {
					monitor.done();
				}
			}

		};
		try {
			getContainer().run(true, false, op);
		} catch (final InterruptedException e) {
			return false;
		} catch (final InvocationTargetException e) {
			final Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}

		return true;

	}

	private void doFinish(String projectRoot, final String projectName,
			final IProject project, final String machineFileName,
			final IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Creating " + projectName
				+ " BMotion Studio Visualization", 2);

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(projectRoot));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throw new CoreException(new Status(IStatus.ERROR,
					"org.eventb.internal.ui", IStatus.OK, "Project \""
							+ projectRoot + "\" does not exist.", null));
		}

		RodinCore.run(new IWorkspaceRunnable() {

			public void run(final IProgressMonitor pMonitor)
					throws CoreException {

				String fMachineName = machineFileName.replace(".bum", ".bcm")
						.replace(".buc", ".bcc");

				IFolder bmotionFolder = project.getFolder("bmotion");
				if (!bmotionFolder.exists())
					bmotionFolder.create(IResource.NONE, true, null);

				IFolder visFolder = bmotionFolder.getFolder(projectName);
				if (!visFolder.exists())
					visFolder.create(IResource.NONE, true, null);

				try {
					createFile(visFolder, "index.html", createHTMLContent());
					createFile(visFolder, "script.js", createJsContent());
					createFile(visFolder, "bmotion.json",
							createConfigurationContent(fMachineName));
					// createFile(
					// visFolder,
					// "script.groovy",
					// new ByteArrayInputStream("// Put your code here"
					// .getBytes()));
					createFile(visFolder, "style.css",
							new ByteArrayInputStream("".getBytes()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}

		}, monitor);

		monitor.worked(1);

	}

	private ByteArrayInputStream createJsContent() {
		String content = "requirejs(['bmotion'], function () {\n"
				+ "// Put your code here\n" + "});";
		return new ByteArrayInputStream(content.getBytes());
	}

	private ByteArrayInputStream createConfigurationContent(String machineName) {
		String content = "{" + "'name': 'lift',\n"
				+ "'template': 'index.html',\n" + "'model': '../" + machineName
				+ "',\n" + "'tool': 'BAnimation',\n" + "}";
		return new ByteArrayInputStream(content.getBytes());
	}

	private ByteArrayInputStream createHTMLContent() {
		String content = "<html data-bms-visualisation>\n"
				+ "  <head>\n"
				+ "      <title>BMotion Studio for ProB</title>\n"
				+ "      <link rel='stylesheet' type='text/css' href='style.css'>\n"
				+ "  </head>\n" + "  <body>\n"
				+ "   <script data-main='script' src='require.js'></script>\n"
				+ "  </body>\n" + "</html>\n";
		return new ByteArrayInputStream(content.getBytes());
	}

	/*
	 * private InputStream getInputStream(String name) throws IOException {
	 * Bundle bundle = Platform.getBundle("de.prob2.ui.rodin"); URL fileURL =
	 * bundle.getEntry("resources/" + name); return
	 * fileURL.openConnection().getInputStream(); }
	 */

	private IFile createFile(IFolder folder, String name,
			InputStream inputStream) throws CoreException, IOException {
		IFile f = folder.getFile(name);
		f.create(inputStream, IResource.NONE, null);
		return f;
	}

	@Override
	public void addPages() {
		page = new NewBMotionProjectWizardPage(selection);
		addPage(page);
	}

}
