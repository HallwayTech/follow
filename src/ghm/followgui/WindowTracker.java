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

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
Implementation of {@link java.awt.event.WindowListener} which writes the
position and size of a window to a FollowAppAttributes object each time windowClosing()
is invoked.

@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
public class WindowTracker extends WindowAdapter {

  /**
  Construct a new WindowTracker which will write window position/size to the 
  supplied FollowAppAttributes object. The supplied keys will be used when writing 
  position/size values to the FollowAppAttributes object.
  
  @param attributes attributes object to which window size & position should 
    be written
  */  
    public WindowTracker (FollowAppAttributes attributes) {
    attributes_ = attributes;
  }
  

  /**
  Each time this method is invoked, the position/size of the window which is
  closing will be written to the FollowAppAttributes object.
  */
  public void windowClosing (WindowEvent e) {
    Window window = (Window)e.getSource();
    attributes_.setWidth(window.getWidth());
    attributes_.setHeight(window.getHeight());
    attributes_.setX(window.getX());
    attributes_.setY(window.getY());
  }


  
  protected FollowAppAttributes attributes_;
  
}

