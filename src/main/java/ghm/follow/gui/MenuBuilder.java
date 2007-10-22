package ghm.follow.gui;

import ghm.follow.config.Configure;
import ghm.follow.nav.Bottom;
import ghm.follow.nav.NextTab;
import ghm.follow.nav.PreviousTab;
import ghm.follow.nav.Top;
import ghm.follow.search.ClearAllHighlights;
import ghm.follow.search.ClearHighlights;
import ghm.follow.search.Find;

import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.JMenuBar;

public class MenuBuilder {
	private MenuBuilder() {
	}

	/**
	 * Builds the menu bar for the application
	 * 
	 * @return reference the constructed menu bar
	 */
	public static JMenuBar buildMenuBar(ResourceBundle resources,
			HashMap<String, FollowAppAction> actions) {
		// create menu bar and add menus
		JMenuBar jMenuBar = new JMenuBar();

		// file menu
		Menu fileMenu = MenuBuilder.buildFileMenu(resources, actions);
		jMenuBar.add(fileMenu);
		// edit menu
		Menu editMenu = MenuBuilder.buildEditMenu(resources, actions);
		jMenuBar.add(editMenu);
		// tool menu
		Menu toolsMenu = MenuBuilder.buildToolsMenu(resources, actions);
		jMenuBar.add(toolsMenu);
		// window menu
		Menu windowMenu = MenuBuilder.buildWindowMenu(resources, actions);
		jMenuBar.add(windowMenu);
		// help menu
		Menu helpMenu = MenuBuilder.buildHelpMenu(resources, actions);
		jMenuBar.add(helpMenu);

		return jMenuBar;
	}

	public static Menu buildFileMenu(ResourceBundle resources,
			HashMap<String, FollowAppAction> actions) {
		Menu fileMenu = new Menu(resources.getString("menu.File.name"), resources
				.getString("menu.File.mnemonic"));
		fileMenu.addFollowAppAction(actions.get(Open.NAME));
		fileMenu.addFollowAppAction(actions.get(Close.NAME));
		fileMenu.addFollowAppAction(actions.get(Reload.NAME));
		fileMenu.addSeparator();
		fileMenu.addFollowAppAction(actions.get(Reset.NAME));
		fileMenu.addFollowAppAction(actions.get(Pause.NAME));
		fileMenu.addSeparator();
		Menu recentFilesMenu_ = new Menu(resources.getString("menu.RecentFiles.name"), resources
				.getString("menu.RecentFiles.mnemonic"));
		fileMenu.add(recentFilesMenu_);
		fileMenu.addSeparator();
		fileMenu.addFollowAppAction(actions.get(Exit.NAME));
		return fileMenu;
	}

	public static Menu buildEditMenu(ResourceBundle resources,
			HashMap<String, FollowAppAction> actions) {
		Menu editMenu = new Menu(resources.getString("menu.Edit.name"), resources
				.getString("menu.Edit.mnemonic"));
		editMenu.addFollowAppAction(actions.get(Find.NAME));
		editMenu.addSeparator();
		editMenu.addFollowAppAction(actions.get(ClearHighlights.NAME));
		editMenu.addFollowAppAction(actions.get(ClearAllHighlights.NAME));
		return editMenu;
	}

	public static Menu buildToolsMenu(ResourceBundle resources,
			HashMap<String, FollowAppAction> actions) {
		Menu toolsMenu = new Menu(resources.getString("menu.Tools.name"), resources
				.getString("menu.Tools.mnemonic"));
		toolsMenu.addFollowAppAction(actions.get(Top.NAME));
		toolsMenu.addFollowAppAction(actions.get(Bottom.NAME));
		toolsMenu.addSeparator();
		toolsMenu.addFollowAppAction(actions.get(Clear.NAME));
		toolsMenu.addFollowAppAction(actions.get(ClearAll.NAME));
		toolsMenu.addFollowAppAction(actions.get(Delete.NAME));
		toolsMenu.addFollowAppAction(actions.get(DeleteAll.NAME));
		toolsMenu.addSeparator();
		toolsMenu.addFollowAppAction(actions.get(Configure.NAME));
		toolsMenu.addFollowAppAction(actions.get(Edit.NAME));
		return toolsMenu;
	}

	public static Menu buildWindowMenu(ResourceBundle resources,
			HashMap<String, FollowAppAction> actions) {
		Menu windowMenu = new Menu(resources.getString("menu.Window.name"), resources
				.getString("menu.Window.mnemonic"));
		windowMenu.addFollowAppAction(actions.get(NextTab.NAME));
		windowMenu.addFollowAppAction(actions.get(PreviousTab.NAME));
		return windowMenu;
	}

	public static Menu buildHelpMenu(ResourceBundle resources,
			HashMap<String, FollowAppAction> actions) {
		Menu helpMenu = new Menu(resources.getString("menu.Help.name"), resources
				.getString("menu.Help.mnemonic"));
		helpMenu.addFollowAppAction(actions.get(About.NAME));
		return helpMenu;
	}

	/**
	 * Builds the popup menu shown when right clicking in a text area.
	 * 
	 * @return
	 */
	public static PopupMenu buildPopupMenu(HashMap<String, FollowAppAction> actions) {
		PopupMenu popupMenu = new PopupMenu();
		popupMenu.addFollowAppAction(actions.get(Open.NAME));
		popupMenu.addFollowAppAction(actions.get(Close.NAME));
		popupMenu.addFollowAppAction(actions.get(Reload.NAME));
		popupMenu.addSeparator();
		popupMenu.addFollowAppAction(actions.get(Top.NAME));
		popupMenu.addFollowAppAction(actions.get(Bottom.NAME));
		popupMenu.addSeparator();
		popupMenu.addFollowAppAction(actions.get(Clear.NAME));
		popupMenu.addFollowAppAction(actions.get(ClearAll.NAME));
		popupMenu.addFollowAppAction(actions.get(Delete.NAME));
		popupMenu.addFollowAppAction(actions.get(DeleteAll.NAME));
		popupMenu.addSeparator();
		popupMenu.addFollowAppAction(actions.get(Configure.NAME));
		popupMenu.addFollowAppAction(actions.get(Edit.NAME));
		return popupMenu;
	}

	/**
	 * Builds the toolbar shown at the top of the application
	 * 
	 * @return
	 */
	public static ToolBar buildToolBar(HashMap<String, FollowAppAction> actions) {
		ToolBar toolBar = new ToolBar();
		toolBar.addFollowAppAction(actions.get(Open.NAME));
		toolBar.addSeparator();
		toolBar.addFollowAppAction(actions.get(Top.NAME));
		toolBar.addFollowAppAction(actions.get(Bottom.NAME));
		toolBar.addSeparator();
		toolBar.addFollowAppAction(actions.get(Clear.NAME));
		toolBar.addFollowAppAction(actions.get(ClearAll.NAME));
		toolBar.addFollowAppAction(actions.get(Delete.NAME));
		toolBar.addFollowAppAction(actions.get(DeleteAll.NAME));
		toolBar.addSeparator();
		toolBar.addFollowAppAction(actions.get(Reset.NAME));
		toolBar.addFollowAppAction(actions.get(Pause.NAME));
		toolBar.addSeparator();
		toolBar.addFollowAppAction(actions.get(Configure.NAME));
		return toolBar;
	}
}