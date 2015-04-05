 
package de.codecentric.eclipse.tutorial.app.part;

import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

public class LogViewPart {
	
	ListView<String> viewer;
	
	@PostConstruct
	public void postConstruct(BorderPane parent) {
		viewer = new ListView<String>();
		parent.setCenter(viewer);
	}
	
	@Inject
	@Optional
	void logging(@UIEventTopic("TOPIC_LOGGING") String message) {
		viewer.getItems().add(message);
	}

}