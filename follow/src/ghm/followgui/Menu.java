// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
This class exists as a kludge to get around problems I was having with menu
items being configured by JMenu in a way that was not to my liking.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class Menu extends JMenu {
  
  Menu (String name, String mnemonic) {
    super(name);
    setMnemonic(mnemonic.charAt(0));
  }
  
  void addFollowAppAction (FollowAppAction a) {
    this.add(a);
    JMenuItem menuItem = this.getItem(this.getItemCount() - 1);
    menuItem.setIcon(null);
    menuItem.setMnemonic(a.getMnemonic());
    menuItem.setAccelerator(a.getAccelerator());
  }
  
}

