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

