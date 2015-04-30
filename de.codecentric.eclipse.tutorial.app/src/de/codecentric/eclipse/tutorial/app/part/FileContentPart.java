package de.codecentric.eclipse.tutorial.app.part;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;

import de.codecentric.eclipse.tutorial.app.ContextKeys;

public class FileContentPart {

	Label fileName;
	
	@PostConstruct
	public void postConstruct(BorderPane pane) {
		fileName = new Label();
		pane.setCenter(fileName);
	}
	
	@Inject
	@Optional
	public void setFileContent(@Named(ContextKeys.CURRENT_FILE) String currentFile) {
		if (currentFile != null) {
			fileName.setText(currentFile);
		}
	}
}
