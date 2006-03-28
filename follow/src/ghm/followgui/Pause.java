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

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;

/**
Pauses/unpauses the autoscrolling of files that are open in the Follow application.
@author <a href="mailto:chall@cfhdev.com">Carl Hall</a>
*/
class Pause extends FollowAppAction {
  Pause (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.Pause.name"),
      app.resBundle_.getString("action.Pause.mnemonic"),
      app.resBundle_.getString("action.Pause.accelerator"),
      getIcon(app)
    );
  }

  public void actionPerformed (ActionEvent e) {
    // update the autoscroll property
    app_.attributes_.setAutoScroll(!app_.attributes_.autoScroll());
    // get the icon to be set
    ImageIcon icon = new ImageIcon(app_.getClass().getResource(Pause.getIcon(app_)));
    // set the icon in the action.  when updating here, the icon is changed
    // whether the event is caused by menu, button click or key combo
    app_.pause_.putValue(Action.SMALL_ICON, icon);
  }

  private static String getIcon(FollowApp app) {
    String image = (app.attributes_.autoScroll() 
        ? app.resBundle_.getString("action.Pause.onIcon")
        : app.resBundle_.getString("action.Pause.offIcon"));
    return image;
  }
}