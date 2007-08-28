package ghm.follow.test.gui;

import ghm.follow.gui.Close;
import ghm.follow.gui.Open;
import ghm.follow.nav.Bottom;

import java.io.File;

public class BottomTest extends AppLaunchingTestCase {

	public BottomTest(String name) {
		super(name);
	}

	public void testEnabled() throws Exception {
		assertEquals(false, _app.getAction(Bottom.NAME).isEnabled());
		File file = createTempFile();
		_systemInterface.setFileFromUser(file);
		invokeAction(_app.getAction(Open.NAME));
		assertEquals(true, _app.getAction(Bottom.NAME).isEnabled());
		invokeAction(_app.getAction(Close.NAME));
		assertEquals(false, _app.getAction(Bottom.NAME).isEnabled());
	}
}