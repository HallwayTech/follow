// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
This class exists as a kludge to get around problems I was having with toolbar
items being configured by JToolBar in a way that was not to my liking.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class ToolBar extends JToolBar {

  void addFollowAppAction (FollowAppAction a) {
    this.add(a);
    JButton toolBarItem = 
      (JButton)this.getComponent(this.getComponentCount() - 1);
    toolBarItem.setText(null);
    toolBarItem.setToolTipText((String)a.getValue(Action.NAME));
  }

}

