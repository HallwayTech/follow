package ghm.follow;

import java.io.*;

public class PrintStreamDestinationTest extends BaseTestCase {

  public PrintStreamDestinationTest (String name) { super(name); }

  public void testPrintCalled () throws Exception {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    PrintStreamDestination dest = new PrintStreamDestination(new PrintStream(byteStream));
    follower_ = new FileFollower(followedFile_, new OutputDestination[]{dest});
    follower_.start();
    String control = "control";
    followedFileWriter_.write(control);
    followedFileWriter_.flush();
    Thread.sleep(follower_.getLatency());
    assertEquals(control, new String(byteStream.toByteArray()));
  }

}

