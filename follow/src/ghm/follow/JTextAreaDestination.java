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

package ghm.follow;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
Implementation of {@link OutputDestination} which appends Strings to a
{@link JTextArea}.

@see OutputDestination
@see JTextArea
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
public class JTextAreaDestination implements OutputDestination {

  /**
  Construct a new JTextAreaDestination.
  @param jTextArea text will be appended to this text area
  @param autoPositionCaret if true, caret will be automatically moved 
    to the bottom of the text area when text is appended
  */
  public JTextAreaDestination (JTextArea jTextArea, boolean autoPositionCaret) {
    jTextArea_ = jTextArea;
    autoPositionCaret_ = autoPositionCaret;
  }

  public JTextArea getJTextArea () { return jTextArea_; }
  public void setJTextArea (JTextArea jTextArea) { jTextArea_ = jTextArea; }

  /** @return whether caret will be automatically moved to the bottom of the text area when
    text is appended */
  public boolean autoPositionCaret () { return autoPositionCaret_; }

  /** @param autoPositionCaret if true, caret will be automatically moved to the bottom of 
    the text area when text is appended */
  public void setAutoPositionCaret (boolean autoPositionCaret) { 
    autoPositionCaret_ = autoPositionCaret;
  }

  public void print (String s) {
    jTextArea_.append(s);
    if (autoPositionCaret_) {
      jTextArea_.setCaretPosition(jTextArea_.getDocument().getLength());
    }
  }

  protected JTextArea jTextArea_;
  protected boolean autoPositionCaret_;

}

