package de.codecentric.eclipse.tutorial.app;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class LifecycleManager {

	@PostContextCreate
	public void postContextCreate(IEclipseContext context, 
			@Preference(nodePath="de.codecentric.eclipse.tutorial") IEclipsePreferences preferences) {
		
		context.declareModifiable(ContextKeys.CURRENT_FILE);
		
		// read from preferences
		Deque<String> fileHistory = new ArrayDeque<String>(4);
		Preferences sub = preferences.node("file_history");
		for (int i = 1; i < 5; i++) {
			String filename = sub.get("recentfile." + i, null);
			if (filename != null) {
				fileHistory.add(filename);
			}
		}
		context.set(ContextKeys.FILE_HISTORY, fileHistory);
	}
	
	@PreSave
	public void preSave(
			@Named(ContextKeys.FILE_HISTORY) Deque<String> fileHistory, 
			@Preference(nodePath="de.codecentric.eclipse.tutorial") IEclipsePreferences preferences) {
		Preferences sub = preferences.node("file_history");
		
		// write to preferences
		int counter = 0;
		for (String filename : fileHistory) {
			sub.put("recentfile." + ++counter, filename);
		}
		
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
