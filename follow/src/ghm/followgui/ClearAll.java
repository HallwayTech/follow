// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
Action which clears the contents of all followed files.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class ClearAll extends FollowAppAction {
  
  ClearAll (FollowApp app) throws IOException {
    super(
      app, 
      app.resBundle_.getString("action.ClearAll.name"),
      app.resBundle_.getString("action.ClearAll.mnemonic"),
      app.resBundle_.getString("action.ClearAll.accelerator"),
      app.resBundle_.getString("action.ClearAll.icon")
    );
  }

  public void actionPerformed (ActionEvent e) {
    List allFileFollowingPanes = app_.getAllFileFollowingPanes();
    Iterator i = allFileFollowingPanes.iterator();
    FileFollowingPane fileFollowingPane;
    while (i.hasNext()) {
      fileFollowingPane = (FileFollowingPane)i.next();      
      fileFollowingPane.getTextArea().setText("");
    }
  }

}

