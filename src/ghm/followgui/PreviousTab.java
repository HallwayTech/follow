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

/**
 * Pauses/unpauses the autoscrolling of files that are open in the Follow application.
 * 
 * @author <a href="mailto:chall@cfhdev.com">Carl Hall</a>
 */
class PreviousTab extends FollowAppAction {
  PreviousTab (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.PreviousFile.name"),
      app.resBundle_.getString("action.PreviousFile.mnemonic"),
      app.resBundle_.getString("action.PreviousFile.accelerator"),
      app.resBundle_.getString("action.PreviousFile.icon")
    );
  }

  /**
   * Moves to next tab if not at the last tab.
   * 
   * @param e
   */
  public void actionPerformed (ActionEvent e) {
    int currentIndex = app_.tabbedPane_.getSelectedIndex();
    if (currentIndex > 0) {
      app_.tabbedPane_.setSelectedIndex(currentIndex - 1);
    }
  }
}