package de.codecentric.eclipse.tutorial.app.handler;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.fx.core.ProgressReporter;
import org.eclipse.fx.core.operation.CancelableOperation;
import org.eclipse.fx.core.update.UpdateService;
import org.eclipse.fx.core.update.UpdateService.UpdatePlan;
import org.eclipse.fx.core.update.UpdateService.UpdateResult;
import org.eclipse.fx.ui.services.restart.RestartService;
import org.eclipse.fx.ui.services.sync.UISynchronize;

public class FXUpdateHandler {

	@Execute
	public void execute(UpdateService updateService, UISynchronize sync, RestartService restartService) {
		// Version 1.2
//		updateService.checkUpdate((ucd) -> {
//			if (ucd.nothingToUpdate()) {
//				showMessage(sync, "Nothing to update");
//			}
//			else {
//				if (State.OK.equals(ucd.getState())) {
//					if (showConfirmation(
//							sync, 
//							"Updates available", 
//							"There are updates available. Do you want to install them now?")) {
//						
//						updateService.update(ucd, (data) -> {
//							
//							if (showConfirmation(
//									sync, 
//									"Updates installed, restart?", 
//									"Updates have been installed successfully, do you want to restart?")) {
//								
//								sync.syncExec(() -> restartService.restart(true));
//							}
//						});
//					}
//				}
//			}
//		});
		
		// Version 2.0
		CancelableOperation<Optional<UpdatePlan>> check = updateService.checkUpdate(ProgressReporter.NULLPROGRESS_REPORTER);
		check.onCancel(() -> showMessage(sync, "Operation cancelled"));
		check.onException(t -> {
			String message = t.getStatus().getMessage();
			showError(sync, message);
		});
		check.onComplete((updatePlan) -> {
			if (!updatePlan.isPresent()) {
				showMessage(sync, "Nothing to update");
			}
			else {
				if (showConfirmation(
						sync, 
						"Updates available", 
						"There are updates available. Do you want to install them now?")) {
					
					CancelableOperation<UpdateResult> result = updatePlan.get().runUpdate(ProgressReporter.NULLPROGRESS_REPORTER);
					result.onCancel(() -> showMessage(sync, "Operation cancelled"));
					result.onException(t -> showError(sync, t.getLocalizedMessage()));
					result.onComplete((r) -> {
						if (showConfirmation(
								sync, 
								"Updates installed, restart?", 
								"Updates have been installed successfully, do you want to restart?")) {
					
							sync.syncExec(() -> restartService.restart(true));
						}
					});
				}
			}
		});
	}

	private void showMessage(UISynchronize sync, final String message) {
		// as the provision needs to be executed in a background thread
		// we need to ensure that the message dialog is executed in
		// the UI thread
		sync.syncExec(() -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information");
			alert.setContentText(message);

			alert.showAndWait();
		});
	}

	private void showError(UISynchronize sync, final String message) {
		// as the provision needs to be executed in a background thread
		// we need to ensure that the message dialog is executed in
		// the UI thread
		sync.syncExec(new Runnable() {

			@Override
			public void run() {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setContentText(message);

				alert.showAndWait();
			}
		});
	}
	
	private boolean showConfirmation(UISynchronize sync, final String title, final String message) {
		return sync.syncExec(() -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle(title);
			alert.setContentText(message);
			Optional<ButtonType> result = alert.showAndWait();
			return (result.get() == ButtonType.OK);
		}, false);
	}
}
