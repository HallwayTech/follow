package ghm.follow;

import javax.swing.JTextArea;

public class JTextAreaDestinationTest extends BaseTestCase {

  public JTextAreaDestinationTest (String name) { super(name); }

  public void setUp () throws Exception {
    super.setUp();
    jTextArea_ = new JTextArea();
  }

  public void testNoAutoscroll () throws Exception {
    validateTextContentAndCaretPos(new JTextAreaDestination(jTextArea_, false));
  }

  public void testAutoscroll () throws Exception {
    validateTextContentAndCaretPos(new JTextAreaDestination(jTextArea_, true));
  }

  private void validateTextContentAndCaretPos (JTextAreaDestination destination) throws Exception {
    follower_ = new FileFollower(followedFile_, new OutputDestination[]{destination});
    follower_.start();
    String control = "control";
    writeToFollowedFileAndWait(control);
    assertEquals(control, jTextArea_.getText());
    validateCaretPosition(destination);
    String control2 = "control2";
    writeToFollowedFileAndWait(control2);
    assertEquals(control+control2, jTextArea_.getText());
    validateCaretPosition(destination);
    String control3 = "\n\n230usadlkfjasd;lkfjas\nl;kasdjf;lkajsdf\n\n";
    writeToFollowedFileAndWait(control3);
    assertEquals(control+control2+control3, jTextArea_.getText());
    validateCaretPosition(destination);
  }

  private void validateCaretPosition (JTextAreaDestination destination) {
    int caretPos = destination.getJTextArea().getCaret().getMark();
    if (destination.autoPositionCaret()) {
      assertEquals(destination.getJTextArea().getText().length(), caretPos);
    }
    else {
      assertEquals(0, caretPos);
    }
  }

  private JTextArea jTextArea_;

}

