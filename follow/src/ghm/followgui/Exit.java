// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

/**
Action which exits the Follow application.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class Exit extends FollowAppAction {
  
  Exit (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.Exit.name"),
      app.resBundle_.getString("action.Exit.mnemonic"),
      app.resBundle_.getString("action.Exit.accelerator")
    );
  }

  public void actionPerformed (ActionEvent e) {
    app_.frame_.dispatchEvent(
      new WindowEvent(app_.frame_, WindowEvent.WINDOW_CLOSING)
    );
  }

}

