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
import java.util.Properties;

/**
Implementation of {@link java.awt.event.WindowListener} which writes the
position and size of a window to a Properties object each time windowClosing()
is invoked.

@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
public class WindowTracker extends WindowAdapter {

  /**
  Construct a new WindowTracker which will write window position/size to the 
  supplied Properties object. The supplied keys will be used when writing 
  position/size values to the Properties object.
  
  @param properties Properties object to which window size & position should 
    be written
  @param heightKey key for window height
  @param widthKey key for window width
  @param xKey key for window position, x-dimension
  @param yKey key for window position, y-dimension
  */  
  public WindowTracker (
    Properties properties,
    String heightKey,
    String widthKey,
    String xKey,
    String yKey
  ) {
    properties_ = properties;
    heightKey_ = heightKey;
    widthKey_ = widthKey;
    xKey_ = xKey;
    yKey_ = yKey;
  }
  

  /**
  Each time this method is invoked, the position/size of the window which is
  closing will be written to the Properties object.
  */
  public void windowClosing (WindowEvent e) {
    Window window = (Window)e.getSource();
    setInt(heightKey_, window.getHeight());
    setInt(widthKey_, window.getWidth());
    setInt(xKey_, window.getX());
    setInt(yKey_, window.getY());
  }


  /**
  Convenience method for setting integer values in an instance of Properties
  @param key key to use when setting the integer property
  @param value integer value to be set
  */
  protected void setInt (String key, int value) {
    properties_.setProperty(key, String.valueOf(value));
  }

  
  protected Properties properties_;
  protected String heightKey_;
  protected String widthKey_;
  protected String xKey_;
  protected String yKey_;
  
}

