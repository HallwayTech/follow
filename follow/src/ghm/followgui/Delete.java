/* 
Copyright (C) 2000, 2001 Greg Merrill (greghmerrill@yahoo.com)

This file is part of Follow (http://follow.sf.net).

Follow is free software; you can redistribute it and/or modify
it under the terms of version 2 of the GNU General Public
License as published by the Free Software Foundation.

Follow is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Follow; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package ghm.followgui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
Action which deletes the contents of the currently followed file.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class Delete extends FollowAppAction {
  
  Delete (FollowApp app) throws IOException {
    super(
      app, 
      app.resBundle_.getString("action.Delete.name"),
      app.resBundle_.getString("action.Delete.mnemonic"),
      app.resBundle_.getString("action.Delete.accelerator"),
      app.resBundle_.getString("action.Delete.icon")
    );
  }

  public void actionPerformed (ActionEvent e) {
    if (app_.attributes_.confirmDelete()) {
      DisableableConfirm confirm = new DisableableConfirm(
app_.frame_,
app_.resBundle_.getString("dialog.confirmDelete.title"),
app_.resBundle_.getString("dialog.confirmDelete.message"),
app_.resBundle_.getString("dialog.confirmDelete.confirmButtonText"),
app_.resBundle_.getString("dialog.confirmDelete.doNotConfirmButtonText"),
app_.resBundle_.getString("dialog.confirmDelete.disableText")
      );
      confirm.pack();
      confirm.show();
      if (confirm.markedDisabled()) {
        app_.attributes_.setConfirmDelete(false);
      }
      if (confirm.markedConfirmed()) { performDelete(); }
    }
    else { performDelete(); }
  }
  
  private void performDelete () {
    app_.setCursor(Cursor.WAIT_CURSOR);
    FileFollowingPane fileFollowingPane = app_.getSelectedFileFollowingPane();
    try { fileFollowingPane.clear(); }
    catch (IOException ioe) {
      ioe.printStackTrace(System.err);
      app_.setCursor(Cursor.DEFAULT_CURSOR);
      JOptionPane.showMessageDialog(
        app_.frame_, 
        app_.resBundle_.getString("message.unableToDelete.text"),
        app_.resBundle_.getString("message.unableToDelete.title"),
        JOptionPane.WARNING_MESSAGE
      );
    }
    app_.setCursor(Cursor.DEFAULT_CURSOR);
  }

}

