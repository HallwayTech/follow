package ghm.follow;

public class FileFollowerTest extends BaseTestCase {

  public FileFollowerTest (String name) { super(name); }

  public void setUp () throws Exception {
    super.setUp();
    testination_ = new Testination();
  }

  public void testOutputWritten () throws Exception {
    follower_ = new FileFollower(followedFile_, new OutputDestination[]{testination_});
    follower_.start();
    String control = "control";
    writeToFollowedFileAndWait(control);
    assertEquals(control, testination_.strBuf_.toString());
    String control2 = "control2";
    writeToFollowedFileAndWait(control2);
    assertEquals(control+control2, testination_.strBuf_.toString());
  }

  public void testShortLatency () throws Exception {
    follower_ = new FileFollower(followedFile_, new OutputDestination[]{testination_});
    follower_.setLatency(100);
    follower_.start();
    String control = "control";
    writeToFollowedFileAndWait(control);
    assertEquals(control, testination_.strBuf_.toString());
  }

  public void testSmallBufferSize () throws Exception {
    follower_ = new FileFollower(followedFile_, new OutputDestination[]{testination_});
    follower_.setBufferSize(8);
    follower_.start();
    String control = "32098jaspfj234-08uewrfiojsad;lfkjqw4poiru2340ruwefkjasd;lkjq2po43iu123-4r098uasdfl;asdclkjasdfasdf9834roaerf";
    writeToFollowedFileAndWait(control);
    assertEquals(control, testination_.strBuf_.toString());
  }

  public void testMultipleDestinations () throws Exception {
    Testination testination2 = new Testination();
    follower_ = new FileFollower(followedFile_, new OutputDestination[]{testination_, testination2});
    follower_.start();
    String control = "control";
    writeToFollowedFileAndWait(control);
    assertEquals(control, testination_.strBuf_.toString());
    assertEquals(control, testination2.strBuf_.toString());
  }

  private Testination testination_;

  class Testination implements OutputDestination {
    public void print (String s) { strBuf_.append(s); }
    StringBuffer strBuf_ = new StringBuffer();
  }

}

