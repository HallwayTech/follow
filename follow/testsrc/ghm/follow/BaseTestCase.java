package ghm.follow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import junit.framework.TestCase;

public abstract class BaseTestCase extends TestCase {

  public BaseTestCase (String name) { super(name); }

  public void setUp () throws Exception {
    followedFile_ = File.createTempFile("followedFile", null);
    followedFile_.deleteOnExit();
    followedFileWriter_ = new BufferedWriter(new FileWriter(followedFile_));
  }

  public void tearDown () throws Exception {
    if (follower_ != null) { follower_.stopAndWait(); }
    followedFileWriter_.flush();
    followedFileWriter_.close();
  }

  protected FileFollower follower_;
  protected File followedFile_;
  protected Writer followedFileWriter_;
}

