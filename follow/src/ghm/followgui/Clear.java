// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.event.ActionEvent;
import java.io.IOException;

/**
Action which clears the text area for the currently followed file.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class Clear extends FollowAppAction {
  
  Clear (FollowApp app) throws IOException {
    super(
      app, 
      app.resBundle_.getString("action.Clear.name"),
      app.resBundle_.getString("action.Clear.mnemonic"),
      app.resBundle_.getString("action.Clear.accelerator"),
      app.resBundle_.getString("action.Clear.icon")
    );
  }

  public void actionPerformed (ActionEvent e) {
    FileFollowingPane fileFollowingPane = app_.getSelectedFileFollowingPane();
    fileFollowingPane.getTextArea().setText("");
  }

}

