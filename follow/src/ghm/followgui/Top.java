// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.JViewport;

/**
Action which jumps to the last line of the currently followed file.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class Top extends FollowAppAction {
  
  Top (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.Top.name"),
      app.resBundle_.getString("action.Top.mnemonic"),
      app.resBundle_.getString("action.Top.accelerator"),
      app.resBundle_.getString("action.Top.icon")
    );    
  }

  public void actionPerformed (ActionEvent e) {
    FileFollowingPane fileFollowingPane = app_.getSelectedFileFollowingPane();
    fileFollowingPane.getTextArea().setCaretPosition(0);
    JViewport viewport = fileFollowingPane.getViewport();
    viewport.setViewPosition(new Point(0, 0));
    viewport.revalidate();
  }

}

