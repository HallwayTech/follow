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

import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/**
Base class for all actions in the Follow application.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
abstract class FollowAppAction extends AbstractAction {

  FollowAppAction (
    FollowApp app, 
    String name, 
    String mnemonic,
    String accelerator
  ) {
    super(name);
    init(app, mnemonic, accelerator);
  }
  
  FollowAppAction (
    FollowApp app, 
    String name, 
    String mnemonic,
    String accelerator,
    String iconName
  ) {
    super(name, new ImageIcon(app.getClass().getResource(iconName)));
    init(app, mnemonic, accelerator);
  }
  
  private void init (FollowApp app, String mnemonic, String accelerator) {
    app_ = app;
    setMnemonic(mnemonic);
    setAccelerator(
      KeyStroke.getKeyStroke(accelerator.charAt(0), KeyEvent.CTRL_MASK)
    );
  }
  
  char getMnemonic () { return mnemonic_; }
  void setMnemonic (char mnemonic) { mnemonic_ = mnemonic; }
  void setMnemonic (String mnemonic) {
    mnemonic_ = mnemonic.charAt(0);
  }
  
  KeyStroke getAccelerator () { return accelerator_; }
  void setAccelerator (KeyStroke accelerator) { accelerator_ = accelerator; }  
  
  FollowApp app_;
  char mnemonic_;
  KeyStroke accelerator_;
  
}

