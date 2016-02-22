/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.eventb;

import java.math.BigInteger;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import de.prob.animator.IAnimator;
import de.prob.animator.command.GetDefaultPreferencesCommand;
import de.prob.animator.domainobjects.ProBPreference;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.servlet.Main;
import de.prob2.ui.eclipse.ErrorHandler;

/**
 * A preferences page extension (Window->Preferences) to change the general
 * settings for animating machines
 *
 * @author Jens Bendisposto, Lukas Diekmann
 *
 */
public class ProBGeneralPreferences extends FieldEditorPreferencePage implements
IWorkbenchPreferencePage {

	private static final PrologTerm PREF_INT = new CompoundPrologTerm("int");
	private static final PrologTerm PREF_NAT = new CompoundPrologTerm("nat");
	private static final PrologTerm PREF_NAT1 = new CompoundPrologTerm("nat1");
	private static final PrologTerm PREF_BOOL = new CompoundPrologTerm("bool");
	private static final PrologTerm PREF_STRING = new CompoundPrologTerm(
			"string");

	private final Preferences eclipsePrefs;
	private final List<ProBPreference> probPrefs;
	private final ProBPreferenceStore store = new ProBPreferenceStore();



	public ProBGeneralPreferences() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(store.getStore());
		eclipsePrefs = Platform.getPreferencesService().getRootNode().node(
				InstanceScope.SCOPE).node("prob_animation_preferences");

		final IAnimator animator = Main.getInjector().getInstance(IAnimator.class);
		final GetDefaultPreferencesCommand cmd = new GetDefaultPreferencesCommand();
		animator.execute(cmd);
		probPrefs = cmd.getPreferences();

		store.load();
		try {
			// forces the application to save the preferences
			eclipsePrefs.flush();
		} catch (final BackingStoreException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected Control createContents(final Composite parent) {
		final Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText("ProB Settings");
		return super.createContents(group);
	}

	@Override
	protected void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		for (final ProBPreference p : probPrefs) {
			final FieldEditor editor = createEditor(p, parent);
			addField(editor);
		}
	}

	@Override
	public boolean performOk() {
		super.performOk();
		store.save();
		try {
			eclipsePrefs.flush();
		} catch (final BackingStoreException e) {
			final String message = "Problem while storing preferences. "
					+ e.getLocalizedMessage();
			ErrorHandler.errorMessage(message);
		}
		return true;
	}

	private FieldEditor createEditor(final ProBPreference pref,
			final Composite parent) {
		final PrologTerm type = pref.type;
		final String name = pref.name;
		final String desc = pref.description;
		final FieldEditor field;

		if (PREF_INT.equals(type)) {
			field = createIntField(name, desc, parent, Integer.MIN_VALUE,
					Integer.MAX_VALUE);
		} else if (PREF_NAT.equals(type)) {
			field = createIntField(name, desc, parent, 0, Integer.MAX_VALUE);
		} else if (PREF_NAT1.equals(type)) {
			field = createIntField(name, desc, parent, 1, Integer.MAX_VALUE);
		} else if (PREF_STRING.equals(type)) {
			field = new StringFieldEditor(name, desc, parent);
		} else if (PREF_BOOL.equals(type)) {
			field = new BooleanFieldEditor(name, desc,
					BooleanFieldEditor.SEPARATE_LABEL, parent);
		} else if (type.hasFunctor("range", 2)) {
			final CompoundPrologTerm range = (CompoundPrologTerm) type;
			final BigInteger lower = ((IntegerPrologTerm) range.getArgument(1))
					.getValue();
			final BigInteger upper = ((IntegerPrologTerm) range.getArgument(2))
					.getValue();
			field = createIntField(name, desc, parent, lower.intValue(), upper
					.intValue());
		} else if (type.isList()) {
			final ListPrologTerm typelist = (ListPrologTerm) type;
			final String[][] comboEntries = new String[typelist.size()][2];
			for (int i = 0; i < typelist.size(); i++) {
				final String value = typelist.get(i).toString();
				comboEntries[i][0] = value;
				comboEntries[i][1] = value;
			}
			field = new ComboFieldEditor(name, desc, comboEntries, parent);
		} else {
			field = new StringFieldEditor(name, desc, parent);
		}
		return field;
	}

	private FieldEditor createIntField(final String name, final String desc,
			final Composite parent, final int min, final int max) {
		final IntegerFieldEditor intfield = new IntegerFieldEditor(name, desc,
				parent);
		intfield.setValidRange(min, max);
		return intfield;
	}

	public class ProBPreferenceStore {

		private final PreferenceStore store;

		public ProBPreferenceStore() {
			store = new PreferenceStore("ProB");
		}

		public void load() {
			for (final ProBPreference p : probPrefs) {
				store
				.setValue(p.name, eclipsePrefs.get(p.name,
						p.defaultValue));
				store.setDefault(p.name, p.defaultValue);
			}
		}

		public void save() {
			for (final ProBPreference p : probPrefs) {
				eclipsePrefs.put(p.name, store.getString(p.name));
			}
		}

		public PreferenceStore getStore() {
			return store;
		}

	}

	@Override
	public void init(IWorkbench workbench) {
	}

}
