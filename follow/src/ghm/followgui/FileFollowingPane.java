// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import ghm.follow.FileFollower;
import ghm.follow.JTextAreaDestination;
import ghm.follow.OutputDestination;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
A component which allows one to view a text file to which information is
being asynchronously appended.

@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
public class FileFollowingPane extends JScrollPane {

  /**
  @param file text file which is to be followed
  @param bufferSize size of the character buffer inside the FileFollower
    used to follow the supplied file
  @param latency latency of the FileFollower used to follow the supplied file
  */
  public FileFollowingPane (File file, int bufferSize, int latency) {
    textArea_ = new JTextArea();
    fileFollower_ = new FileFollower(
      file,
      bufferSize,
      latency,
      new OutputDestination [] { new JTextAreaDestination(textArea_) }
    );
    this.getViewport().setView(textArea_);
  }

  /**
  Returns the text area to which the followed file's contents are being printed.
  @return text area containing followed file's contents
  */
  public JTextArea getTextArea () { return textArea_; }

  /**
  Returns the FileFollower which is being used to print information in this
  component.  
  @return FileFollower used by this component
  */
  public FileFollower getFileFollower () { return fileFollower_; }

  /**
  Convenience method; equivalent to calling getFileFollower().getFollowedFile()
  */  
  public File getFollowedFile () { return fileFollower_.getFollowedFile(); }

  /**
  Convenience method; equivalent to calling getFileFollower().start()
  */  
  public void startFollowing () { fileFollower_.start(); }

  /**
  Convenience method; equivalent to calling getFileFollower().stop()
  */  
  public void stopFollowing () { fileFollower_.stop(); }

  /**
  Convenience method; equivalent to calling getFileFollower().stopAndWait()
  */  
  public void stopFollowingAndWait () throws InterruptedException { 
    fileFollower_.stopAndWait(); 
  }

  /**
  Clears the contents of this FileFollowingPane synchronously.
  */
  public void clear () throws IOException {
    if (fileFollower_.getFollowedFile().length() == 0L) { return; }
    synchronized (fileFollower_) {
      try {
        fileFollower_.stopAndWait();
      }
      catch (InterruptedException interruptedException) {
        // Handle this better later
        interruptedException.printStackTrace(System.err);
      }

      // This has the effect of clearing the contents of the followed file
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(
        fileFollower_.getFollowedFile()
      ));
      bos.close();

      // Update textarea contents to reflect freshly cleared file
      Document doc = textArea_.getDocument();
      try {
        doc.remove(0, doc.getLength());
      }
      catch (BadLocationException badLocationException) {
        // Handle this better later
        badLocationException.printStackTrace(System.err);
      }

      fileFollower_.start();
    }
  }
  
  /** FileFollower used to print to this component */  
  protected FileFollower fileFollower_;
  
  /** Text area into which followed file's contents are printed */
  protected JTextArea textArea_;
  
}

