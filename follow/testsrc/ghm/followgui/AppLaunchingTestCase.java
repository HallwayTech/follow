package ghm.followgui;

import java.io.File;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import junit.framework.TestCase;

public abstract class AppLaunchingTestCase extends TestCase {

  public AppLaunchingTestCase (String name) { super(name); }

  public void setUp () throws Exception {
    if (FollowAppAttributes.propertyFile.exists()) {
      FollowAppAttributes.propertyFile.delete();
    }
    FollowApp.main(new String[0]);
    app_ = FollowApp.instance_;
    systemInterface_ = new TestSystemInterface();
    app_.systemInterface_ = systemInterface_;
  }

  public void tearDown () throws Exception {
    invokeAction(app_.exit_);
    FollowAppAttributes.propertyFile.delete();
  }

  protected void invokeAndWait (Runnable runnable) {
    try { SwingUtilities.invokeAndWait(runnable); }
    catch (Exception e) { throw new RuntimeException(e.getMessage()); }
  }

  protected void invokeAction (final Action action) {
    invokeAndWait(new Runnable () { public void run () { action.actionPerformed(null); } });
  }

  protected File createTempFile () throws IOException {
    File file = File.createTempFile("followedFile", null);
    file.deleteOnExit();
    return file;
  }

  protected FollowApp app_;
  protected TestSystemInterface systemInterface_;
}

