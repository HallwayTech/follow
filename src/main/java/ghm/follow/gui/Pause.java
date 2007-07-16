/**
 * This file is part of Follow (http://follow.sf.net).
 * 
 * Follow is free software; you can redistribute it and/or modify it under the
 * terms of version 2 of the GNU General Public License as published by the Free
 * Software Foundation.
 * 
 * Follow is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Follow; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package ghm.follow.gui;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 * Pauses/unpauses the autoscrolling of files that are open in the Follow
 * application.
 * 
 * @author <a href="mailto:chall@cfhdev.com">Carl Hall</a>
 */
public class Pause extends FollowAppAction {
	public static final String NAME = "pause";

	public Pause(FollowApp app) {
		super(app, app.getResourceBundle().getString("action.Pause.name"), app.getResourceBundle()
				.getString("action.Pause.mnemonic"), app.getResourceBundle().getString(
				"action.Pause.accelerator"), getIcon(app.getAttributes().autoScroll(), app
				.getResourceBundle()));
	}

	/**
	 * Handles actions performed relating to pause or playing a log's following
	 * 
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		playPausePane(getApp().getSelectedFileFollowingPane());
	}

	/**
	 * Plays or pauses a pane depending on it's current state. If following, it
	 * pauses. If not following, it plays.
	 * 
	 * @param pane
	 */
	public void playPausePane(FileFollowingPane pane) {
		if (pane.isFollowing()) {
			pane.pauseFollowing();
		}
		else {
			pane.unpauseFollowing();
		}
		setIconByState(pane.isFollowing());
	}

	/**
	 * Sets the icon of this action based on the provided follow state
	 * 
	 * @param follow
	 */
	public void setIconByState(boolean follow) {
		// get the icon to be set
		ImageIcon icon = new ImageIcon(getApp().getClass().getResource(
				Pause.getIcon(follow, getApp().getResourceBundle())));
		// set the icon in the action. when updating here, the icon is changed
		// whether the event is caused by menu, button click or key combo
		getApp().getAction(Pause.NAME).putValue(Action.SMALL_ICON, icon);
	}

	private static String getIcon(boolean following, ResourceBundle resBundle) {
		String image = (following ? resBundle.getString("action.Pause.onIcon") : resBundle
				.getString("action.Pause.offIcon"));
		return image;
	}
}