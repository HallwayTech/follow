// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JViewport;

/**
Action which jumps to the last line of the currently followed file.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class Bottom extends FollowAppAction {
  
  Bottom (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.Bottom.name"),
      app.resBundle_.getString("action.Bottom.mnemonic"),
      app.resBundle_.getString("action.Bottom.accelerator"),
      app.resBundle_.getString("action.Bottom.icon")
    );    
  }

  public void actionPerformed (ActionEvent e) {
    FileFollowingPane fileFollowingPane = app_.getSelectedFileFollowingPane();
    JTextArea textArea = fileFollowingPane.getTextArea();
    textArea.setCaretPosition(textArea.getDocument().getLength());
    JViewport viewport = fileFollowingPane.getViewport();
    int y = (int)(
      viewport.getViewSize().getHeight() - viewport.getExtentSize().getHeight()
    );
    Point bottomPosition = new Point(0, y);
    viewport.setViewPosition(bottomPosition);
    viewport.revalidate();
  }

}

