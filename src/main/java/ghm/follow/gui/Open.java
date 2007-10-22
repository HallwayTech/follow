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

package ghm.follow.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;

import javax.swing.JOptionPane;

/**
 * Action which opens a new file in the Follow application.
 * 
 * @author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 * @author <a href="mailto:carl.hall@gmail.com">Carl Hall</a>
 */
public class Open extends FollowAppAction {
	public static final String NAME = "open";

	private File recentFile;

	public Open(FollowApp app) {
		super(app, FollowApp.getResourceBundle().getString("action.Open.name"), FollowApp.getResourceBundle()
				.getString("action.Open.mnemonic"), FollowApp.getResourceBundle().getString(
				"action.Open.accelerator"), FollowApp.getResourceBundle().getString("action.Open.icon"));
	}

	public Open(FollowApp app, File recentFile) {
		super(app, recentFile.getAbsolutePath(), FollowApp.getResourceBundle().getString(
				"action.Open.mnemonic"), FollowApp.getResourceBundle().getString(
				"action.Open.accelerator"));
		this.recentFile = recentFile;
	}

	public void actionPerformed(ActionEvent e) {
		File f = null;
		try {
			if (recentFile != null) {
				f = recentFile;
				getApp().open(recentFile, getApp().getAttributes().autoScroll());
			} else {
				f = getApp().getSystemInterface().getFileFromUser();
				if (f != null) {
					getApp().open(f, getApp().getAttributes().autoScroll());
				}
			}
		} catch (FileNotFoundException ex) {
			String msg = MessageFormat.format(FollowApp.getResourceBundle().getString(
					"message.cmdLineFileNotFound.text"), new Object[] { f });
			JOptionPane.showMessageDialog(getApp().getFrame(), msg, FollowApp.getResourceBundle()
					.getString("message.filesDeletedSinceLastExecution.title"),
					JOptionPane.WARNING_MESSAGE);
		}

	}
}