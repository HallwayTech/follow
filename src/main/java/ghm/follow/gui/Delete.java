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
import org.apache.log4j.Logger;

import javax.swing.JOptionPane;

/**
 * Action which deletes the contents of the currently followed file.
 * 
 * @author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
public class Delete extends FollowAppAction {
	public static final String NAME = "delete";

	public Delete(FollowApp app) throws IOException {
		super(app, app.getResourceBundle().getString("action.Delete.name"), app.getResourceBundle()
				.getString("action.Delete.mnemonic"), app.getResourceBundle().getString(
				"action.Delete.accelerator"), app.getResourceBundle().getString(
				"action.Delete.icon"));
	}

	public void actionPerformed(ActionEvent e) {
		if (getApp().getAttributes().confirmDelete()) {
			DisableableConfirm confirm = new DisableableConfirm(getApp().getFrame(), getApp()
					.getResourceBundle().getString("dialog.confirmDelete.title"), getApp()
					.getResourceBundle().getString("dialog.confirmDelete.message"), getApp()
					.getResourceBundle().getString("dialog.confirmDelete.confirmButtonText"), getApp()
					.getResourceBundle().getString("dialog.confirmDelete.doNotConfirmButtonText"),
					getApp().getResourceBundle().getString("dialog.confirmDelete.disableText"));
			confirm.pack();
			confirm.setVisible(true);
			if (confirm.markedDisabled()) {
				getApp().getAttributes().setConfirmDelete(false);
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
		FileFollowingPane fileFollowingPane = getApp().getSelectedFileFollowingPane();
		try {
			fileFollowingPane.clear();
		}
		catch (IOException ioe) {
			getLog().error("IOException in Delete", ioe);
			getApp().setCursor(Cursor.DEFAULT_CURSOR);
			JOptionPane.showMessageDialog(getApp().getFrame(), getApp().getResourceBundle().getString(
					"message.unableToDelete.text"), getApp().getResourceBundle().getString(
					"message.unableToDelete.title"), JOptionPane.WARNING_MESSAGE);
		}
		getApp().setCursor(Cursor.DEFAULT_CURSOR);
	}

	private transient Logger log;

	private Logger getLog() {
		if (log == null) {
			log = Logger.getLogger(Delete.class.getName());
		}
		return log;
	}
}