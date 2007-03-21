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

package ghm.follow;

import ghm.follow.search.SearchableTextPane;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;

/**
Implementation of {@link OutputDestination} which appends Strings to a
{@link JTextArea}.

@see OutputDestination
@see JTextArea
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
public class JTextPaneDestination implements OutputDestination {

  /**
  Construct a new JTextAreaDestination.
  @param jTextArea text will be appended to this text area
  @param autoPositionCaret if true, caret will be automatically moved 
    to the bottom of the text area when text is appended
  */
  public JTextPaneDestination (JTextPane jTextPane, boolean autoPositionCaret) {
    jTextPane_ = jTextPane;
    autoPositionCaret_ = autoPositionCaret;
  }

  public JTextPane getJTextPane() { return jTextPane_; }
  public void setJTextArea (JTextPane jTextPane) { jTextPane_ = jTextPane; }

  /** @return whether caret will be automatically moved to the bottom of the text area when
    text is appended */
  public boolean autoPositionCaret () { return autoPositionCaret_; }

  /** @param autoPositionCaret if true, caret will be automatically moved to the bottom of 
    the text area when text is appended */
  public void setAutoPositionCaret (boolean autoPositionCaret) { 
    autoPositionCaret_ = autoPositionCaret;
  }

  public void print(String s) {
		try {
			Style style = null;
			if (jTextPane_ instanceof SearchableTextPane) {
				style = ((SearchableTextPane) jTextPane_).getDefaultStyle();
			}
			jTextPane_.getDocument().insertString(jTextPane_.getDocument().getLength(), s, style);
			if (autoPositionCaret_) {
				jTextPane_.setCaretPosition(jTextPane_.getDocument().getLength());
			}
		}
		catch (BadLocationException e) {
			// just ignore, nothing we can do
		}
	}

  public void clear () {
    jTextPane_.setText("");
    if (autoPositionCaret_) {
      jTextPane_.setCaretPosition(0);
    }
  }

  protected JTextPane jTextPane_;
  protected boolean autoPositionCaret_;

}