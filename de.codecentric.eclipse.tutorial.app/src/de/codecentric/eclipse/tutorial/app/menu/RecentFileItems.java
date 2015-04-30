package de.codecentric.eclipse.tutorial.app.menu;

import java.io.File;
import java.util.Deque;
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuSeparator;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import de.codecentric.eclipse.tutorial.app.ContextKeys;

public class RecentFileItems {
	
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items, EModelService modelService, MApplication application,
			@Named(ContextKeys.FILE_HISTORY) Deque<String> fileHistory) {
		
		int counter = 0;
		for (String file : fileHistory) {
			List<MCommand> commands = modelService.findElements(application, "de.codecentric.eclipse.tutorial.app.command.open", MCommand.class, null);
			
			MHandledMenuItem handledItem = modelService.createModelElement(MHandledMenuItem.class);
			handledItem.setLabel(++counter + " " + file.substring(file.lastIndexOf(File.separator)+1));
			handledItem.setElementId("open_recent_file_" + counter);
			handledItem.setCommand(commands.get(0));
			
			//set parameter
			MParameter parameter = modelService.createModelElement(MParameter.class);
			parameter.setName("de.codecentric.eclipse.tutorial.app.commandparameter.fileToOpen");
			parameter.setValue(file);
			handledItem.getParameters().add(parameter);
			
			items.add(handledItem);
		}
		
		if (fileHistory.size() > 0) {
			items.add(modelService.createModelElement(MMenuSeparator.class));
		}
	}
		
}