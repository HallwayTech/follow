// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.follow;

/**
Interface used by a {@link FileFollower} to print the contents of a followed
file.
@see FileFollower
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
public interface OutputDestination {

  /**
  Print the supplied String.
  @param s String to be printed
  */
  public void print (String s) ;
  
}

