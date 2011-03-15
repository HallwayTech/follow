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

	protected static FollowApp app;
	protected static MockSystemInterface systemInterface;
	protected static String propertyFileName;

	public static void denyExit() {
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

	public static void launch(String[] argv) throws Exception {
		String[] args = appendPropFileArg(argv);
		FollowApp.main(args);
		doPostLaunch();
	}

	protected static void doPostLaunch() throws Exception {
		app = FollowApp.getInstance();
		systemInterface = new MockSystemInterface();
		app.setSystemInterface(systemInterface);
	}

	public static void shutdown() throws Exception {
		invokeAction(app.getAction(Exit.NAME));
		while (!systemInterface.exitCalled()) {
			Thread.sleep(250);
		}
		File propFile = new File(propertyFileName);
		propFile.delete();
	}

	protected static void invokeAndWait(Runnable runnable) {
		try {
			SwingUtilities.invokeAndWait(runnable);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	protected static void invokeAction(final Action action) {
		invokeAndWait(new Runnable() {
			public void run() {
				action.actionPerformed(null);
			}
		});
	}

	protected static String[] appendPropFileArg(String[] argv) {
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