// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

/**
This class exists as a kludge to get around problems I was having with popup
menu items being configured by JPopupMenu in a way that was not to my liking.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class PopupMenu extends JPopupMenu {
  
  void addFollowAppAction (FollowAppAction a) {
    JMenuItem menuItem = this.add(a);
    menuItem.setIcon(null);
    menuItem.setMnemonic(a.getMnemonic());
    menuItem.setAccelerator(a.getAccelerator());
  }
  
}

