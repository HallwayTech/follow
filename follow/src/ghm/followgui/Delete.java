// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

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

