package ghm.followgui;

import java.io.File;

/**
Various system calls are routed through an instance of this class;
this enables test code to interject itself where appropriate by
assigning a different instance of SystemInterface to {@link FollowApp}.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
interface SystemInterface {

  /**
  Normally, this method should delegate to a file chooser or 
  other appropriate file selection mechanism.  However, it can
  be overriden by tests to return temporary files.

  @return the File selected by the user
  */
  public File getFileFromUser () ;

}

