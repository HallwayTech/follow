// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class WhatIsThis extends JButton {
  
  WhatIsThis (
    final FollowApp app, 
    final String title, 
    final String text) 
  {
    super(getWhatIsThisIcon(app));
    setBorderPainted(false);
    setToolTipText(title);
    setMargin(new Insets(0, 0, 0, 0));
    addActionListener(new ActionListener () {
      public void actionPerformed (ActionEvent e) {
        JOptionPane.showMessageDialog(
          app.frame_,
          text,
          title,
          JOptionPane.INFORMATION_MESSAGE
        );
      }
    });
  }

  static Icon getWhatIsThisIcon (FollowApp app) {
    if (whatIsThisIcon == null) {
      whatIsThisIcon = new ImageIcon(app.getClass().getResource(
        app.resBundle_.getString("WhatIsThis.icon")
      ));
    }
    return whatIsThisIcon;
  }

  static Icon whatIsThisIcon;
  
}

