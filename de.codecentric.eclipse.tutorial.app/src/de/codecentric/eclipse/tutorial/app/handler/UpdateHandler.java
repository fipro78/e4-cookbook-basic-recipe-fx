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
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.fx.ui.services.sync.UISynchronize;

public class UpdateHandler {

    @Execute
    public void execute(IProvisioningAgent agent, UISynchronize sync, IWorkbench workbench) {
        
         // use a simple job
        Job updateJob = new Job("Update") {
            
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                return update(agent, sync, workbench);
            }
        };
        
        updateJob.schedule();
            
            // update synchronously
//            update(agent);
            
    }

    private IStatus update(final IProvisioningAgent agent, UISynchronize sync, IWorkbench workbench) {
        // configure update operation
        ProvisioningSession session = new ProvisioningSession(agent);
        // update the whole running profile, otherwise specify IUs
        UpdateOperation operation = new UpdateOperation(session);
        
        //check if updates are available
        IStatus status = operation.resolveModal(null);
        if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
            showMessage(sync, "Nothing to update");
            return Status.CANCEL_STATUS;
        }
        else {
            final ProvisioningJob provisioningJob = operation.getProvisioningJob(null);
            if (provisioningJob != null) {
                provisioningJob.addJobChangeListener(new JobChangeAdapter() {
                    @Override
                    public void done(IJobChangeEvent event) {
                      if (event.getResult().isOK()) {
                          sync.syncExec(new Runnable() {
                            
                            @Override
                            public void run() {
                                Alert alert = new Alert(AlertType.CONFIRMATION);
                                alert.setTitle("Updates installed, restart?");
                                alert.setContentText("Updates have been installed successfully, do you want to restart?");

                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == ButtonType.OK){
                                	workbench.restart();
                                }
                            }
                        });
                      }
                      super.done(event);
                    }
                });
                
                provisioningJob.schedule();
//                return provisioningJob.runModal(sub.newChild(100));
//                if (status.getSeverity() == IStatus.CANCEL)
//                    return Status.CANCEL_STATUS;
            }
            else {
                if (operation.hasResolved()) {
                    showError(sync, "Couldn't get provisioning job: " + operation.getResolutionResult());
                }
                else {
                    showError(sync, "Couldn't resolve provisioning job");
                }
                return Status.CANCEL_STATUS;
            }
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
}
