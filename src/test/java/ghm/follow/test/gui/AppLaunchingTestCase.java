package ghm.follow.test.gui;

import ghm.follow.FollowApp;
import ghm.follow.config.FollowAppAttributes;
import ghm.follow.gui.Exit;
import ghm.follow.test.BaseTestCase;

import java.io.File;
import java.security.Permission;

import javax.swing.Action;
import javax.swing.SwingUtilities;

public abstract class AppLaunchingTestCase extends BaseTestCase {

    protected FollowApp app;
    protected MockSystemInterface systemInterface;
    protected String propertyFileName;
    protected boolean manageApp = true;

    public static void beforeClass() {
	SecurityManager securityManager = new SecurityManager() {
	    @Override
	    public void checkPermission(Permission permission) {
		if ("exitVM".equals(permission.getName())) {
		    // throw exception here to keep the exit from happening.
		    throw new SecurityException(
			    "System.exit attempted and blocked.");
		}
	    }
	};
	System.setSecurityManager(securityManager);
    }

    public static void afterClass() {
	FollowApp instance = FollowApp.getInstance();
	instance.getFrame().dispose();
    }

    @Override
    public void setUp() throws Exception {
	super.setUp();
	if (manageApp) {
	    String[] args = appendPropFileArg(null);
	    FollowApp.main(args);
	    doPostLaunch();
	}
    }

    protected void doPostLaunch() throws Exception {
	app = FollowApp.getInstance();
	systemInterface = new MockSystemInterface();
	app.setSystemInterface(systemInterface);
    }

    @Override
    public void tearDown() throws Exception {
	if (manageApp) {
	    invokeAction(app.getAction(Exit.NAME));
	    while (!systemInterface.exitCalled()) {
		Thread.sleep(250);
	    }
	    File propFile = new File(propertyFileName);
	    propFile.delete();
	}
	super.tearDown();
    }

    protected void invokeAndWait(Runnable runnable) {
	try {
	    SwingUtilities.invokeAndWait(runnable);
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    protected void invokeAction(final Action action) {
	invokeAndWait(new Runnable() {
	    public void run() {
		action.actionPerformed(null);
	    }
	});
    }

    protected String[] appendPropFileArg(String[] argv) {
	propertyFileName = System.getProperty("java.io.tmpdir")
		+ System.getProperty("file.separator")
		+ FollowAppAttributes.PROPERTY_FILE_NAME;
	int length = ((argv != null) ? argv.length : 0) + 2;
	String[] args = new String[length];
	for (int i = 0; i < args.length - 2; i++) {
	    args[i] = argv[i];
	}
	args[length - 2] = "-propFile";
	args[length - 1] = propertyFileName;
	return args;
    }
}