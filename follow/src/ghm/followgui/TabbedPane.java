/* 
Copyright (C) 2000-2003 Greg Merrill (greghmerrill@yahoo.com)

This file is part of Follow (http://follow.sf.net).

Follow is free software; you can redistribute it and/or modify
it under the terms of version 2 of the GNU General Public
License as published by the Free Software Foundation.

Follow is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Follow; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

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

