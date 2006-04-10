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

/**
 * Action which clears the highlighted search in the current pane
 * 
 * @author <a href="mailto:chall@cfhdev.com">Carl Hall</a>
 */
class ClearHighlights extends FollowAppAction {
  
  ClearHighlights (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.ClearHighlights.name"),
      app.resBundle_.getString("action.ClearHighlights.mnemonic"),
      app.resBundle_.getString("action.ClearHighlights.accelerator")
    );
  }

  public void actionPerformed (ActionEvent e) {
    // get the current selected tab
    FileFollowingPane pane = (FileFollowingPane) app_.tabbedPane_.getSelectedComponent();
    // search the tab with the given text
    SearchableTextArea textArea = (SearchableTextArea) pane.getTextArea();
    textArea.removeHighlights();
  }
}