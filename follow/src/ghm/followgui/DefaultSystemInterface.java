package ghm.followgui;

import java.awt.Cursor;
import java.io.File;
import javax.swing.JFileChooser;

/**
Default implementation of {@link SystemInterface} for
the Follow application.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class DefaultSystemInterface implements SystemInterface {

  public DefaultSystemInterface (FollowApp app) { app_ = app; }

  public File getFileFromUser () {
    app_.setCursor(Cursor.WAIT_CURSOR);
    JFileChooser chooser = new JFileChooser(
      app_.attributes_.getLastFileChooserDirectory()
    );
    app_.setCursor(Cursor.DEFAULT_CURSOR);
    int returnVal = chooser.showOpenDialog(app_.frame_);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      return chooser.getSelectedFile();
    }
    return null;
  }

  protected FollowApp app_;
}

