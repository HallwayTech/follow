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
class Reset extends FollowAppAction {

  Reset (FollowApp app) throws IOException {
    super(
      app,
      app.resBundle_.getString("action.Reset.name"),
      app.resBundle_.getString("action.Reset.mnemonic"),
      app.resBundle_.getString("action.Reset.accelerator"),
      app.resBundle_.getString("action.Reset.icon")
    );
  }

  public void actionPerformed (ActionEvent e) {
    FileFollowingPane fileFollowingPane = app_.getSelectedFileFollowingPane();
    fileFollowingPane.restartFollowing();
  }
}