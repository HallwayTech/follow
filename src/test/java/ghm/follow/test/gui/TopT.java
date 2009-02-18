package ghm.follow.test.gui;

import static org.junit.Assert.assertFalse;
import ghm.follow.nav.Top;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TopT extends AppLaunchingTestCase {
    @BeforeClass
    public static void beforeClass() throws Exception {
	AppLaunchingTestCase.launch(null);
    }

    @Override
    @Before
    public void setUp() throws Exception {
	super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {
	super.tearDown();
    }

    @AfterClass
    public static void afterClass() throws Exception {
	AppLaunchingTestCase.shutdown();
    }

    @Test
    public void topNotEnabled() {
	assertFalse(app.getAction(Top.NAME).isEnabled());
    }
}