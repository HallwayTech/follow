// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JTabbedPane;

/**
Derived from a workaround proposed by sqrrrl for bug 
<a href="http://developer.java.sun.com/developer/bugParade/bugs/4193463.html"
 >4193463</a>. This bug was causing drag-and-drop to behave incorrectly for
all but the first tab.
*/
class TabbedPane extends JTabbedPane {

  TabbedPane (int tabPlacement) { super(tabPlacement); }

  /** sqrrrl's fix */
  public Component findComponentAt (int x, int y) {
    if (!contains(x, y)) { return null; }
    int ncomponents = getComponentCount();
    for (int i = 0 ; i < ncomponents ; i++) {
      Component comp = getComponentAt(i);
      if (comp != null) {
        if (comp instanceof Container) {
          if (comp.isVisible()) {
            comp = ((Container)comp).findComponentAt(
              x - comp.getX(),  
              y - comp.getY()
            );
          }
        } 
        else {
          comp = comp.getComponentAt(x - comp.getX(), y - comp.getY());
        }
        if (comp != null && comp.isVisible()) {
          return comp;
        }
      }
    }
    return this;
  }
    
}

