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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;

import javax.swing.JOptionPane;

/**
 * Action which deletes the contents of all followed files.
 * 
 * @author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
public class DeleteAll extends FollowAppAction {
	public static final String NAME = "deleteAll";

	public DeleteAll(FollowApp app) throws IOException {
		super(app, FollowApp.getResourceBundle().getString("action.DeleteAll.name"), FollowApp
				.getResourceBundle().getString("action.DeleteAll.mnemonic"), FollowApp
				.getResourceBundle().getString("action.DeleteAll.accelerator"), FollowApp
				.getResourceBundle().getString("action.DeleteAll.icon"));
	}

	public void actionPerformed(ActionEvent e) {
		if (getApp().getAttributes().confirmDeleteAll()) {
			DisableableConfirm confirm = new DisableableConfirm(getApp().getFrame(), FollowApp
					.getResourceBundle().getString("dialog.confirmDeleteAll.title"), FollowApp
					.getResourceBundle().getString("dialog.confirmDeleteAll.message"), FollowApp
					.getResourceBundle().getString("dialog.confirmDeleteAll.confirmButtonText"),
					FollowApp.getResourceBundle().getString(
							"dialog.confirmDeleteAll.doNotConfirmButtonText"), FollowApp
							.getResourceBundle().getString("dialog.confirmDeleteAll.disableText"));
			confirm.pack();
			confirm.setVisible(true);
			if (confirm.markedDisabled()) {
				getApp().getAttributes().setConfirmDeleteAll(false);
			}
			if (confirm.markedConfirmed()) {
				performDelete();
			}
		}
		else {
			performDelete();
		}
	}

	private void performDelete() {
		getApp().setCursor(Cursor.WAIT_CURSOR);
		List<FileFollowingPane> allFileFollowingPanes = getApp().getAllFileFollowingPanes();
		try {
			for (FileFollowingPane fileFollowingPane : allFileFollowingPanes) {
				fileFollowingPane.clear();
			}
			getApp().setCursor(Cursor.DEFAULT_CURSOR);
		}
		catch (IOException ioe) {
			getLog().error("IOException error in DeleteAll", ioe);
			getApp().setCursor(Cursor.DEFAULT_CURSOR);
			JOptionPane.showMessageDialog(getApp().getFrame(), FollowApp.getResourceBundle().getString(
					"message.unableToDeleteAll.text"), FollowApp.getResourceBundle().getString(
					"message.unableToDeleteAll.title"), JOptionPane.WARNING_MESSAGE);
		}
	}

	private transient Logger log;

	private Logger getLog() {
		if (log == null) {
			log = Logger.getLogger(DeleteAll.class.getName());
		}
		return log;
	}
}