// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

/**
Action which deletes the contents of all followed files.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class DeleteAll extends FollowAppAction {
  
  DeleteAll (FollowApp app) throws IOException {
    super(
      app, 
      app.resBundle_.getString("action.DeleteAll.name"),
      app.resBundle_.getString("action.DeleteAll.mnemonic"),
      app.resBundle_.getString("action.DeleteAll.accelerator"),
      app.resBundle_.getString("action.DeleteAll.icon")
    );
  }

  public void actionPerformed (ActionEvent e) {
    if (app_.attributes_.confirmDeleteAll()) {
      DisableableConfirm confirm = new DisableableConfirm(
app_.frame_,
app_.resBundle_.getString("dialog.confirmDeleteAll.title"),
app_.resBundle_.getString("dialog.confirmDeleteAll.message"),
app_.resBundle_.getString("dialog.confirmDeleteAll.confirmButtonText"),
app_.resBundle_.getString("dialog.confirmDeleteAll.doNotConfirmButtonText"),
app_.resBundle_.getString("dialog.confirmDeleteAll.disableText")
      );
      confirm.pack();
      confirm.show();
      if (confirm.markedDisabled()) {
        app_.attributes_.setConfirmDeleteAll(false);
      }
      if (confirm.markedConfirmed()) { performDelete(); }
    }
    else { performDelete(); }
  }
  
  private void performDelete() {
    app_.setCursor(Cursor.WAIT_CURSOR);
    List allFileFollowingPanes = app_.getAllFileFollowingPanes();
    Iterator i = allFileFollowingPanes.iterator();
    FileFollowingPane fileFollowingPane;
    try {
      while (i.hasNext()) {
        fileFollowingPane = (FileFollowingPane)i.next();      
        fileFollowingPane.clear();
      }
      app_.setCursor(Cursor.DEFAULT_CURSOR);
    }
    catch (IOException ioe) {
      ioe.printStackTrace(System.err);
      app_.setCursor(Cursor.DEFAULT_CURSOR);
      JOptionPane.showMessageDialog(
        app_.frame_, 
        app_.resBundle_.getString("message.unableToDeleteAll.text"),
        app_.resBundle_.getString("message.unableToDeleteAll.title"),
        JOptionPane.WARNING_MESSAGE
      );
    }
  }

}

