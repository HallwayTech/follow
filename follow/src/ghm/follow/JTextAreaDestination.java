// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

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
  
  public JTextAreaDestination (JTextArea jTextArea) {
    jTextArea_ = jTextArea;
  }

  public JTextArea getJTextArea () { return jTextArea_; }
  public void setJTextArea (JTextArea jTextArea) { jTextArea_ = jTextArea; }
  
  public void print (String s) {
    jTextArea_.append(s);
  }
  
  protected JTextArea jTextArea_;
  
}

