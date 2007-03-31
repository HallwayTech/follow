package ghm.follow.test;

import ghm.follow.FileFollower;
import ghm.follow.JTextPaneDestination;
import ghm.follow.OutputDestination;

import javax.swing.JTextPane;

public class JTextPaneDestinationTest extends BaseTestCase {

	public JTextPaneDestinationTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		jTextPane_ = new JTextPane();
	}

	public void testNoAutoscroll() throws Exception {
		validateTextContentAndCaretPos(new JTextPaneDestination(jTextPane_, false));
	}

	public void testAutoscroll() throws Exception {
		validateTextContentAndCaretPos(new JTextPaneDestination(jTextPane_, true));
	}

	private void validateTextContentAndCaretPos(JTextPaneDestination destination) throws Exception {
		follower_ = new FileFollower(followedFile_, new OutputDestination[] { destination });
		follower_.start();
		String control = "control";
		writeToFollowedFileAndWait(control);
		assertEquals(control, jTextPane_.getText());
		validateCaretPosition(destination);
		String control2 = "control2";
		writeToFollowedFileAndWait(control2);
		assertEquals(control + control2, jTextPane_.getText());
		validateCaretPosition(destination);
		String control3 = "\n\n230usadlkfjasd;lkfjas\nl;kasdjf;lkajsdf\n\n";
		writeToFollowedFileAndWait(control3);
		assertEquals(control + control2 + control3, jTextPane_.getText());
		validateCaretPosition(destination);
	}

	private void validateCaretPosition(JTextPaneDestination destination) {
		int caretPos = destination.getJTextPane().getCaret().getMark();
		if (destination.autoPositionCaret()) {
			assertEquals(destination.getJTextPane().getText().length(), caretPos);
		}
		else {
			assertEquals(0, caretPos);
		}
	}

	private JTextPane jTextPane_;

}
