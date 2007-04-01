package ghm.follow.test;

import ghm.follow.FileFollower;
import ghm.follow.JTextComponentDestination;
import ghm.follow.OutputDestination;

import javax.swing.JTextArea;

public class JTextComponentDestinationTest extends BaseTestCase {

	public JTextComponentDestinationTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		jTextArea_ = new JTextArea();
	}

	public void testNoAutoscroll() throws Exception {
		validateTextContentAndCaretPos(new JTextComponentDestination(jTextArea_, false));
	}

	public void testAutoscroll() throws Exception {
		validateTextContentAndCaretPos(new JTextComponentDestination(jTextArea_, true));
	}

	private void validateTextContentAndCaretPos(JTextComponentDestination destination) throws Exception {
		follower_ = new FileFollower(followedFile_, new OutputDestination[] { destination });
		follower_.start();
		String control = "control";
		writeToFollowedFileAndWait(control);
		assertEquals(control, jTextArea_.getText());
		validateCaretPosition(destination);
		String control2 = "control2";
		writeToFollowedFileAndWait(control2);
		assertEquals(control + control2, jTextArea_.getText());
		validateCaretPosition(destination);
		String control3 = "\n\n230usadlkfjasd;lkfjas\nl;kasdjf;lkajsdf\n\n";
		writeToFollowedFileAndWait(control3);
		assertEquals(control + control2 + control3, jTextArea_.getText());
		validateCaretPosition(destination);
	}

	private void validateCaretPosition(JTextComponentDestination destination) {
		int caretPos = destination.getJTextComponent().getCaret().getMark();
		if (destination.autoPositionCaret()) {
			assertEquals(destination.getJTextComponent().getText().length(), caretPos);
		}
		else {
			assertEquals(0, caretPos);
		}
	}

	private JTextArea jTextArea_;

}
