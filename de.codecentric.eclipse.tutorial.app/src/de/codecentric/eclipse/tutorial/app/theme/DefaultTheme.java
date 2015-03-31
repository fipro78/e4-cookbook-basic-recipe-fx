package de.codecentric.eclipse.tutorial.app.theme;

import org.eclipse.fx.ui.theme.AbstractTheme;

public class DefaultTheme extends AbstractTheme {

	public DefaultTheme() {
		super("theme.default", "Default Theme", 
				DefaultTheme.class.getClassLoader().getResource("css/default.css"));
	}

}
