package de.codecentric.eclipse.tutorial.app.handler;

import java.io.File;
import java.util.Deque;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import de.codecentric.eclipse.tutorial.app.ContextKeys;

public class OpenHandler {
	
	@Execute
	public void execute(
			Stage stage,
			@Optional @Named("de.codecentric.eclipse.tutorial.app.commandparameter.fileToOpen") String fileToOpen, 
			@Named(ContextKeys.FILE_HISTORY) Deque<String> fileHistory, 
			IEclipseContext context) {
		
		if (fileToOpen == null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open File");
			File file = fileChooser.showOpenDialog(stage);
			fileToOpen = file.getName();
		}
		
		if (fileToOpen != null) {
			if (fileHistory.size() == 4) {
				fileHistory.removeLast();
			}
			
			// if file is already in history, remove
			if (fileHistory.contains(fileToOpen)) {
				fileHistory.remove(fileToOpen);
			}
			
			fileHistory.offerFirst(fileToOpen);
			
			context.modify(ContextKeys.CURRENT_FILE, fileToOpen);
		}
	}
		
}