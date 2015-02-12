package de.codecentric.eclipse.tutorial.inverter.part;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import javax.annotation.PostConstruct;

import de.codecentric.eclipse.tutorial.inverter.helper.StringInverter;

public class InverterPart {
	
	@PostConstruct
	public void postConstruct(GridPane parent) {
		Label inputLabel = new Label();
		inputLabel.setText("String to revert:");
		GridPane.setConstraints(inputLabel, 0, 0);
		GridPane.setMargin(inputLabel, new Insets(5.0));
		
		final TextField input = new TextField();
		GridPane.setConstraints(input, 1, 0);
		GridPane.setHgrow(input, Priority.ALWAYS);
		GridPane.setMargin(input, new Insets(5.0));
		
		Button button = new Button();
		button.setText("Revert");
		GridPane.setConstraints(button, 2, 0);
		GridPane.setMargin(button, new Insets(5.0));
		
		Label outputLabel = new Label();
		outputLabel.setText("Inverted String:");
		GridPane.setConstraints(outputLabel, 0, 1);
		GridPane.setMargin(outputLabel, new Insets(5.0));
		
		final Label output = new Label();
		GridPane.setConstraints(output, 1, 1);
		GridPane.setColumnSpan(output, 2);
		GridPane.setHgrow(output, Priority.ALWAYS);
		GridPane.setMargin(output, new Insets(5.0));
		
		button.setOnMouseClicked((e) -> 
			output.setText(StringInverter.invert(input.getText())));

		input.setOnKeyPressed(event -> {
			if (KeyCode.ENTER.equals(event.getCode())) {
				output.setText(StringInverter.invert(input.getText()));
			}
		});
		
		// don't forget to add children to gridpane
		parent.getChildren().addAll(
				inputLabel, input, button, outputLabel, output);
	}
}