/* 
Copyright (C) 2000, 2001 Greg Merrill (greghmerrill@yahoo.com)

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

import java.awt.event.ActionEvent;
import java.io.File;

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
    File file = app_.systemInterface_.getFileFromUser();
    if (file != null) {
      app_.attributes_.setLastFileChooserDirectory(file.getParentFile());
      app_.open(file, true);
    }
  }

}

