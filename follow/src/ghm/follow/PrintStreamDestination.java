// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.follow;

import java.io.PrintStream;

/**
Implementation of {@link OutputDestination} which prints Strings to a
{@link PrintStream}.

@see OutputDestination
@see PrintStream
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
public class PrintStreamDestination implements OutputDestination {

  public PrintStreamDestination (PrintStream printStream) {
    printStream_ = printStream;
  }

  public PrintStream getPrintStream () { return printStream_; }
  public void setPrintStream (PrintStream printStream) {
    printStream_ = printStream;
  }
  
  public void print (String s) {
    printStream_.print(s);
  }
  
  protected PrintStream printStream_;
  
}

