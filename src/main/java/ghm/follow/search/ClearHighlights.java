/*
 * Copyright (C) 2000-2003 Greg Merrill (greghmerrill@yahoo.com)
 * 
 * This file is part of Follow (http://follow.sf.net).
 * 
 * Follow is free software; you can redistribute it and/or modify it under the
 * terms of version 2 of the GNU General Public License as published by the Free
 * Software Foundation.
 * 
 * Follow is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Follow; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package ghm.follow.search;

import ghm.follow.gui.FileFollowingPane;
import ghm.follow.FollowApp;
import ghm.follow.gui.FollowAppAction;

import java.awt.event.ActionEvent;

/**
 * Action which clears the highlighted search in the current pane
 * 
 * @author <a href="mailto:carl.hall@gmail.com">Carl Hall</a>
 */
public class ClearHighlights extends FollowAppAction {
	public static final String NAME = "clearHighlights";

	public ClearHighlights(FollowApp app) {
		super(
				app,
				FollowApp.getResourceString("action.ClearHighlights.name"),
				FollowApp.getResourceString("action.ClearHighlights.mnemonic"),
				FollowApp
						.getResourceString("action.ClearHighlights.accelerator"),
				ActionContext.SINGLE_FILE);
	}

	public void actionPerformed(ActionEvent e) {
		// get the current selected tab
		FileFollowingPane pane = getApp().getSelectedFileFollowingPane();
		// search the tab with the given text
		SearchableTextPane textArea = pane.getTextPane();
		textArea.removeHighlights();
	}
}