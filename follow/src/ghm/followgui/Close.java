// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.event.ActionEvent;

/**
Action which closes the currently followed file.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class Close extends FollowAppAction {
  
  Close (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.Close.name"),
      app.resBundle_.getString("action.Close.mnemonic"),
      app.resBundle_.getString("action.Close.accelerator")
    );
  }

  public void actionPerformed (ActionEvent e) {
    FileFollowingPane fileFollowingPane = app_.getSelectedFileFollowingPane();
    app_.tabbedPane_.removeTabAt(app_.tabbedPane_.getSelectedIndex());
    app_.disableDragAndDrop(fileFollowingPane.getTextArea());  
    app_.attributes_.removeFollowedFile(fileFollowingPane.getFollowedFile());
    fileFollowingPane.stopFollowing();
    app_.fileToFollowingPaneMap_.remove(fileFollowingPane.getFollowedFile());
    if (app_.fileToFollowingPaneMap_.size() == 0) {
      app_.close_.setEnabled(false);
      app_.top_.setEnabled(false);
      app_.bottom_.setEnabled(false);
      app_.clear_.setEnabled(false);
      app_.clearAll_.setEnabled(false);
      app_.delete_.setEnabled(false);
      app_.deleteAll_.setEnabled(false);
    }
  }

}

