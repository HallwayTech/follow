// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;

/**
Action which opens a new file in the Follow application.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class Open extends FollowAppAction {
  
  Open (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.Open.name"),
      app.resBundle_.getString("action.Open.mnemonic"),
      app.resBundle_.getString("action.Open.accelerator"),
      app.resBundle_.getString("action.Open.icon")
    );    
  }

  public void actionPerformed (ActionEvent e) {
    app_.setCursor(Cursor.WAIT_CURSOR);
    JFileChooser chooser = new JFileChooser(
      app_.attributes_.getLastFileChooserDirectory()
    );
    app_.setCursor(Cursor.DEFAULT_CURSOR);
    int returnVal = chooser.showOpenDialog(app_.frame_);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      app_.attributes_.setLastFileChooserDirectory(file.getParentFile());
      app_.open(file, true);
    }
  }

}

