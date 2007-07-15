package ghm.follow.test.gui;

import ghm.follow.config.FollowAppAttributes;
import ghm.follow.gui.Exit;
import ghm.follow.gui.FollowApp;

import java.io.File;

public class CommandLineTest extends AppLaunchingTestCase {

  public CommandLineTest (String name) { super(name); }

  public void setUp () throws Exception {
    if (FollowAppAttributes.PROPERTY_FILE.exists()) {
      FollowAppAttributes.PROPERTY_FILE.delete();
    }
  }

  public void testNoArgs () throws Exception {
    FollowApp.main(new String[0]);
    doPostLaunch();
    assertEquals(false, 
      FollowApp.getInstance().getAttributes().getFollowedFiles().length == 0);
  }

  public void testOneArg () throws Exception {
    File temp = createTempFile();
    FollowApp.main(new String[]{temp.toString()});
    doPostLaunch();
    File[] followedFiles = FollowApp.getInstance().getAttributes().getFollowedFiles();
    File followedFile = (File) followedFiles[0];
    assertEquals(false, followedFiles.length > 1);
    assertEquals(temp, followedFile);
    invokeAction(app_.getAction(Exit.NAME));
    while (!systemInterface_.exitCalled()) { Thread.sleep(250); }
    FollowApp.main(new String[]{temp.toString()});
    doPostLaunch();
    followedFiles = FollowApp.getInstance().getAttributes().getFollowedFiles();
    followedFile = (File) followedFiles[1];
    assertEquals(false, followedFiles.length > 2);
    assertEquals(temp, followedFile);
  }

  public void testTwoArgs () throws Exception {
    File[] temp = new File[]{createTempFile(), createTempFile()};
    FollowApp.main(new String[]{temp[0].toString(), temp[1].toString()});
    doPostLaunch();
    File[] followedFiles = FollowApp.getInstance().getAttributes().getFollowedFiles();
    assertEquals(temp[0], followedFiles[0]);
    assertEquals(temp[1], followedFiles[1]);
    assertEquals(false, followedFiles.length > 2);
  }

  public void testDuplicateArgs () throws Exception {
    File temp = createTempFile();
    FollowApp.main(new String[]{
      temp.toString(), createTempFile().toString(), temp.toString()
    });
    doPostLaunch();
    File[] followedFiles = FollowApp.getInstance().getAttributes().getFollowedFiles();
    assertEquals(temp, followedFiles[0]);
    assertEquals(false, followedFiles.length > 2);
  }
}