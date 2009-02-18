package ghm.follow.test.gui;

import static org.junit.Assert.assertFalse;
import ghm.follow.nav.Top;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class TopT extends AppLaunchingTestCase {
    @Override
    @Before
    public void setUp() throws Exception {
	super.manageApp = false;
	super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {
	super.manageApp = true;
	super.tearDown();
    }

    @AfterClass
    public static void afterClass() {
	AppLaunchingTestCase.afterClass();
    }

    @Test
    public void topNotEnabled() {
	assertFalse(app.getAction(Top.NAME).isEnabled());
    }
}