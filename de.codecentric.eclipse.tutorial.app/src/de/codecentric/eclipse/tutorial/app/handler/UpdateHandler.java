package de.codecentric.eclipse.tutorial.app.handler;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.fx.ui.services.restart.RestartService;
import org.eclipse.fx.ui.services.sync.UISynchronize;

public class UpdateHandler {

	boolean cancelled = false;
	
	@Execute
	public void execute(IProvisioningAgent agent, UISynchronize sync, RestartService restartService) {

		// use a simple job
		Job updateJob = new Job("Update") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return update(agent, sync, restartService);
			}
		};

		updateJob.schedule();
	}

	private IStatus update(final IProvisioningAgent agent, UISynchronize sync, RestartService restartService) {
		// configure update operation
		ProvisioningSession session = new ProvisioningSession(agent);
		// update the whole running profile, otherwise specify IUs
		UpdateOperation operation = new UpdateOperation(session);

		// check if updates are available
		IStatus status = operation.resolveModal(null);
		if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
			showMessage(sync, "Nothing to update");
			return Status.CANCEL_STATUS;
		} 
		else {
			final ProvisioningJob provisioningJob = operation.getProvisioningJob(null);
			if (provisioningJob != null) {
				if (showConfirmation(
						sync, 
						"Updates available", 
						"There are updates available. Do you want to install them now?")) {
					
					provisioningJob.addJobChangeListener(new JobChangeAdapter() {
						@Override
						public void done(IJobChangeEvent event) {
							if (event.getResult().isOK()) {
								if (showConfirmation(
										sync, 
										"Updates installed, restart?", 
										"Updates have been installed successfully, do you want to restart?")) {
							
									// restart the workbench with clearPersistedState
									sync.syncExec(() -> restartService.restart(true));
								}
							}
							else {
								showError(sync, event.getResult().getMessage());
								cancelled = true;
							}
						}
					});

					provisioningJob.schedule();
				}
				else {
					cancelled = true;
				}
			}
			else {
				if (operation.hasResolved()) {
					showError(sync, "Couldn't get provisioning job: " + operation.getResolutionResult());
				} 
				else {
					showError(sync, "Couldn't resolve provisioning job");
				}
				cancelled = true;
			}
		}

		if (cancelled) {
			// reset cancelled flag
			cancelled = false;
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	private void showMessage(UISynchronize sync, final String message) {
		// as the provision needs to be executed in a background thread
		// we need to ensure that the message dialog is executed in
		// the UI thread
		sync.syncExec(new Runnable() {

			@Override
			public void run() {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information");
				alert.setContentText(message);

				alert.showAndWait();
			}
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
