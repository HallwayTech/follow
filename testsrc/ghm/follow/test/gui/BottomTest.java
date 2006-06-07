package ghm.follow.test.gui;

import ghm.follow.gui.Close;
import ghm.follow.gui.Open;
import ghm.follow.nav.Bottom;

import java.io.File;

public class BottomTest extends AppLaunchingTestCase {

  public BottomTest (String name) { super(name); }

  public void testEnabled () throws Exception {
    assertEquals(false, app_.getAction(Bottom.NAME).isEnabled());
    File file = createTempFile();
    systemInterface_.setFileFromUser(file);
    invokeAction(app_.getAction(Open.NAME));
    assertEquals(true, app_.getAction(Bottom.NAME).isEnabled());
    invokeAction(app_.getAction(Close.NAME));
    assertEquals(false, app_.getAction(Bottom.NAME).isEnabled());
  }

}

