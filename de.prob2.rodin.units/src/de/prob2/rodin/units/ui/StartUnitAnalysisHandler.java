/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob2.rodin.units.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariable;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.servlet.Main;
import de.prob.units.UnitAnalysis;
import de.prob2.rodin.units.pragmas.InferredUnitPragmaAttribute;
import de.prob2.rodin.units.pragmas.UnitPragmaAttribute;
import de.prob2.rodin.units.problems.IncorrectUnitDefinitionMarker;
import de.prob2.rodin.units.problems.MultipleUnitsInferredMarker;
import de.prob2.rodin.units.problems.NoUnitInferredMarker;

public class StartUnitAnalysisHandler extends AbstractHandler implements
IHandler {
	private ISelection fSelection;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		fSelection = HandlerUtil.getCurrentSelection(event);

		// Get the Selection
		final IEventBRoot rootElement = getRootElement();
		final IFile resource = extractResource(rootElement);

		if (resource != null) {

			removeUnitErrorMarkers(resource);

			try {
				// load machine and activate plugin
				final UnitAnalysis ua = Main.getInjector().getInstance(
						UnitAnalysis.class);

				String fileName = rootElement.getResource().getRawLocation()
						.makeAbsolute().toOSString();
				if (fileName.endsWith(".buc")) {
					fileName = fileName.replace(".buc", ".bcc");
				} else {
					fileName = fileName.replace(".bum", ".bcm");
				}
				final CompoundPrologTerm result = ua.run(fileName);

				processResults(result);
			} catch (final RodinDBException e) {
				throw new ExecutionException(
						"Unit Analysis Failed with a RodinDBException", e);
			}
		}
		return null;
	}

	private void removeUnitErrorMarkers(final IFile resource) {
		final IProject project = resource.getProject();
		try {
			final IMarker[] markers = project.findMarkers(
					"org.eclipse.core.resources.problemmarker", true,
					IResource.DEPTH_INFINITE);
			for (final IMarker iMarker : markers) {
				if (iMarker.getAttribute(RodinMarkerUtil.ERROR_CODE, "")
						.equals(MultipleUnitsInferredMarker.ERROR_CODE)) {
					iMarker.delete();
				}
				if (iMarker.getAttribute(RodinMarkerUtil.ERROR_CODE, "")
						.equals(IncorrectUnitDefinitionMarker.ERROR_CODE)) {
					iMarker.delete();
				}
				if (iMarker.getAttribute(RodinMarkerUtil.ERROR_CODE, "")
						.equals(NoUnitInferredMarker.ERROR_CODE)) {
					iMarker.delete();
				}
			}

		} catch (final CoreException e1) {
			// TODO
		}
	}

	private void processResults(final CompoundPrologTerm result)
			throws RodinDBException, ExecutionException {
		// preprocess the list into a map
		final Map<String, String> variables = new HashMap<String, String>();
		final List<String> offendingDefinitions = new ArrayList<String>();

		final ListPrologTerm liste = BindingGenerator.getList(result.getArgument(1));

		for (final PrologTerm term : liste) {
			if (term.isAtom()) {
				// this is an error message. do something about it.
				String offendingUnitDefinition = PrologTerm.atomicString(term)
						.replace("Incorrect unit definition: ['", "");
				offendingUnitDefinition = offendingUnitDefinition.replace("']",
						"");

				// add error to the list of incorrect definitions. error markers
				// will be attached later
				offendingDefinitions.add(offendingUnitDefinition);

			} else {
				// process inferred units and add to map
				CompoundPrologTerm compoundTerm;
				compoundTerm = BindingGenerator
						.getCompoundTerm(term, "bind", 2);

				variables.put(
						PrologTerm.atomicString(compoundTerm.getArgument(1)),
						PrologTerm.atomicString(compoundTerm.getArgument(2)));
			}
		}

		final IEventBRoot rootElement = getRootElement();
		// look up the variables / constants of the selected machine in
		// the state
		// and set the inferredUnitPragma attribute
		if (rootElement instanceof IMachineRoot) {
			// find and update variables
			final IVariable[] allVariables = rootElement.getMachineRoot()
					.getVariables();
			for (final IVariable var : allVariables) {
				// reset inferred unit
				var.setAttributeValue(InferredUnitPragmaAttribute.ATTRIBUTE,
						"", new NullProgressMonitor());

				final String variableName = var.getIdentifierString();
				if (variables.containsKey(variableName)) {
					var.setAttributeValue(
							InferredUnitPragmaAttribute.ATTRIBUTE,
							variables.get(variableName),
							new NullProgressMonitor());

					if (variables.get(variableName).startsWith("multiple")) {
						var.createProblemMarker(
								InferredUnitPragmaAttribute.ATTRIBUTE,
								new MultipleUnitsInferredMarker(variableName));
					}
					if (variables.get(variableName).equals("unknown")) {
						var.createProblemMarker(
								InferredUnitPragmaAttribute.ATTRIBUTE,
								new NoUnitInferredMarker(variableName));
					}
				}

				// check if the attached unit pragma (given by user) was marked
				// as offending
				if (var.hasAttribute(UnitPragmaAttribute.ATTRIBUTE)) {
					if (offendingDefinitions.contains(var
							.getAttributeValue(UnitPragmaAttribute.ATTRIBUTE))) {
						var.createProblemMarker(
								InferredUnitPragmaAttribute.ATTRIBUTE,
								new IncorrectUnitDefinitionMarker(variableName));
					}
				}
			}

		} else if (rootElement instanceof IContextRoot) {
			// find and update constants
			final IConstant[] allConstants = rootElement.getContextRoot()
					.getConstants();

			for (final IConstant cst : allConstants) {
				// reset inferred unit
				cst.setAttributeValue(InferredUnitPragmaAttribute.ATTRIBUTE,
						"", new NullProgressMonitor());

				final String constantName = cst.getIdentifierString();
				if (variables.containsKey(constantName)) {
					cst.setAttributeValue(
							InferredUnitPragmaAttribute.ATTRIBUTE,
							variables.get(constantName),
							new NullProgressMonitor());

					if (variables.get(constantName).equals("error")) {
						cst.createProblemMarker(
								InferredUnitPragmaAttribute.ATTRIBUTE,
								new MultipleUnitsInferredMarker(constantName));
					}
					if (variables.get(constantName).equals("unknown")) {
						cst.createProblemMarker(
								InferredUnitPragmaAttribute.ATTRIBUTE,
								new IncorrectUnitDefinitionMarker(constantName));
					}
				}

				// check if the attached unit pragma (given by user) was marked
				// as offending
				if (cst.hasAttribute(UnitPragmaAttribute.ATTRIBUTE)) {
					if (offendingDefinitions.contains(cst
							.getAttributeValue(UnitPragmaAttribute.ATTRIBUTE))) {
						cst.createProblemMarker(
								InferredUnitPragmaAttribute.ATTRIBUTE,
								new MultipleUnitsInferredMarker(constantName));
					}
				}
			}
		} else {
			throw new ExecutionException(
					"Cannot execute unit analysis on this element type. Type of "
							+ rootElement.getComponentName() + " was: "
							+ rootElement.getClass());
		}
	}

	private IEventBRoot getRootElement() {
		IEventBRoot root = null;
		if (fSelection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) fSelection;
			if (ssel.size() == 1) {
				final Object element = ssel.getFirstElement();
				if (element instanceof IEventBRoot) {
					root = (IEventBRoot) element;
				} else if (element instanceof IFile) {
					final IRodinFile rodinFile = RodinCore.valueOf((IFile) element);
					if (rodinFile != null) {
						root = (IEventBRoot) rodinFile.getRoot();
					}
				}
			}
		}
		return root;
	}

	private IFile extractResource(final IEventBRoot rootElement) {
		IFile resource = null;
		if (rootElement == null) {
			resource = null;
		} else if (rootElement instanceof IMachineRoot) {
			resource = ((IMachineRoot) rootElement).getSCMachineRoot()
					.getResource();
		} else if (rootElement instanceof IContextRoot) {
			resource = ((IContextRoot) rootElement).getSCContextRoot()
					.getResource();
		}
		return resource;
	}

}
