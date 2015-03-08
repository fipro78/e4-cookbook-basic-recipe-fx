package de.codecentric.eclipse.tutorial.app.handler;

import java.util.Optional;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;


import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.fx.core.ReturnValue.State;
import org.eclipse.fx.core.update.UpdateService;
import org.eclipse.fx.ui.services.sync.UISynchronize;

public class FXUpdateHandler {

	@Execute
	public void execute(UpdateService service, IWorkbench workbench, UISynchronize sync) {
		service.checkUpdate((ucd) -> {
			
			if (State.OK.equals(ucd.getState())) {
				service.update(ucd, (data) -> {
					
					sync.syncExec(() -> {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Updates installed, restart?");
						alert.setContentText("Updates have been installed successfully, do you want to restart?");
						
						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK){
							workbench.restart();
						}
					});
				});
			}
		});
	}
}
