// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

/**
Action which displays information about the Follow application.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class About extends FollowAppAction {
  
  About (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.About.name"),
      app.resBundle_.getString("action.About.mnemonic"),
      app.resBundle_.getString("action.About.accelerator")
    );
  }

  public void actionPerformed (ActionEvent e) {
    JOptionPane.showMessageDialog(
      app_.frame_, 
      app_.resBundle_.getString("dialog.About.text"),
      app_.resBundle_.getString("dialog.About.title"),
      JOptionPane.INFORMATION_MESSAGE
    );
  }

}

