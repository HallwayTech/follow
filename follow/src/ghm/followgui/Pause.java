/**
 * This file is part of Follow (http://follow.sf.net).
 * 
 * Follow is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * Follow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Follow; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package ghm.followgui;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 * Pauses/unpauses the autoscrolling of files that are open in the Follow application.
 * 
 * @author <a href="mailto:chall@cfhdev.com">Carl Hall</a>
 */
class Pause extends FollowAppAction {
  Pause (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.Pause.name"),
      app.resBundle_.getString("action.Pause.mnemonic"),
      app.resBundle_.getString("action.Pause.accelerator"),
      getIcon(app.attributes_.autoScroll(), app.resBundle_)
    );
  }

  /**
   * Handles actions performed relating to pause or playing a log's following
   * 
   * @param e
   */
  public void actionPerformed (ActionEvent e) {
    FileFollowingPane pane = (FileFollowingPane) app_.tabbedPane_.getSelectedComponent();
    if (pane.isFollowing()) {
      pane.stopFollowing();
    } else {
      pane.startFollowing();
    }
    setIconByState(pane.isFollowing());
  }

  /**
   * Sets the icon of this action based on the provided follow state
   * 
   * @param follow
   */
  public void setIconByState(boolean follow) {
    //  get the icon to be set
    ImageIcon icon = new ImageIcon(app_.getClass().getResource(Pause.getIcon(follow, app_.resBundle_)));
    // set the icon in the action.  when updating here, the icon is changed
    // whether the event is caused by menu, button click or key combo
    app_.pause_.putValue(Action.SMALL_ICON, icon);
  }

  private static String getIcon(boolean following, ResourceBundle resBundle) {
    String image = (following 
        ? resBundle.getString("action.Pause.onIcon")
        : resBundle.getString("action.Pause.offIcon"));
    return image;
  }
}