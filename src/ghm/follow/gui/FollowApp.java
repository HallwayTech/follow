/*
 * Copyright (C) 2000-2003 Greg Merrill (greghmerrill@yahoo.com)
 * 
 * This file is part of Follow (http://follow.sf.net).
 * 
 * Follow is free software; you can redistribute it and/or modify it under the
 * terms of version 2 of the GNU General Public License as published by the Free
 * Software Foundation.
 * 
 * Follow is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Follow; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package ghm.follow.gui;

import ghm.follow.config.Configure;
import ghm.follow.config.FollowAppAttributes;
import ghm.follow.event.WindowTracker;
import ghm.follow.nav.Bottom;
import ghm.follow.nav.NextTab;
import ghm.follow.nav.PreviousTab;
import ghm.follow.nav.Top;
import ghm.follow.search.ClearAllHighlights;
import ghm.follow.search.ClearHighlights;
import ghm.follow.search.Find;
import ghm.follow.search.SearchableTextPane;
import ghm.follow.systemInterface.DefaultSystemInterface;
import ghm.follow.systemInterface.SystemInterface;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * This class' main() method is the entry point into the Follow application.
 * 
 * @see #main(String[])
 * @author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
public class FollowApp {
	private int currentCursor_ = Cursor.DEFAULT_CURSOR;

	private Cursor defaultCursor_;

	private Cursor waitCursor_;

	private FollowAppAttributes attributes_;

	private Map fileToFollowingPaneMap_ = new HashMap();

	private JFrame frame_;

	private JTabbedPane tabbedPane_;

	private ToolBar toolBar_;

	private PopupMenu popupMenu_;

	private Menu recentFilesMenu_;

	private MouseListener rightClickListener_;

	private ResourceBundle resBundle_ = ResourceBundle
			.getBundle("ghm.follow.gui.FollowAppResourceBundle");

	private HashMap actions_ = new HashMap();

	private SystemInterface systemInterface_;

	private static FollowApp instance_;

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public static final String MESSAGE_LINE_SEPARATOR = "\n";

	public static final boolean DEBUG = Boolean.getBoolean("follow.debug");

	public static boolean HAS_SOLARIS_BUG = false;

	public FollowAppAction getAction(String name) {
		return (FollowAppAction) actions_.get(name);
	}

	public void putAction(String name, FollowAppAction action) {
		actions_.put(name, action);
	}

	/**
	 * @param fileNames
	 *            names of files to be opened
	 */
	FollowApp(String[] fileNames) throws IOException, InterruptedException,
			InvocationTargetException {
		// Create & show startup status window
		startupStatus_ = new StartupStatus(getResourceBundle());
		centerWindowInScreen(startupStatus_);
		startupStatus_.pack();
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				startupStatus_.setVisible(true);
			}
		});

		// Ghastly workaround for bug in Font construction, in review by
		// Sun with review id 108683.
		GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				startupStatus_.markDone(startupStatus_.LOAD_SYSTEM_FONTS);
			}
		});

		// create frame first
		frame_ = new JFrame(getResourceBundle().getString("frame.title"));

		// initialize attributes
		attributes_ = new FollowAppAttributes(this);
		for (int i = 0; i < fileNames.length; i++) {
			File file = new File(fileNames[i]);
			if (!file.exists()) {
				String msg = MessageFormat.format(getResourceBundle().getString(
						"message.cmdLineFileNotFound.text"), new Object[] { file });
				System.out.println(msg);
			} else if (!getAttributes().followedFileListContains(file)) {
				getAttributes().addFollowedFile(file);
			}
		}

		// initialize actions
		putAction(Open.NAME, new Open(this));
		putAction(Close.NAME, new Close(this));
		putAction(Reload.NAME, new Reload(this));
		putAction(Edit.NAME, new Edit(this));
		putAction(Exit.NAME, new Exit(this));
		putAction(Top.NAME, new Top(this));
		putAction(Bottom.NAME, new Bottom(this));
		putAction(Clear.NAME, new Clear(this));
		putAction(ClearAll.NAME, new ClearAll(this));
		putAction(Delete.NAME, new Delete(this));
		putAction(DeleteAll.NAME, new DeleteAll(this));
		putAction(Configure.NAME, new Configure(this));
		putAction(About.NAME, new About(this));
		if (DEBUG) {
			putAction(Debug.NAME, new Debug(this));
		}
		putAction(Pause.NAME, new Pause(this));
		putAction(NextTab.NAME, new NextTab(this));
		putAction(PreviousTab.NAME, new PreviousTab(this));
		putAction(Find.NAME, new Find(this));
		putAction(ClearHighlights.NAME, new ClearHighlights(this));
		putAction(ClearAllHighlights.NAME, new ClearAllHighlights(this));
		putAction(Reset.NAME, new Reset(this));

		// initialize SystemInterface
		systemInterface_ = new DefaultSystemInterface(this);

		// initialize menubar
		JMenuBar jMenuBar = buildMenuBar();

		// initialize popupMenu
		buildPopupMenu();

		// initialize toolbar
		buildToolBar();

		// initialize tabbedPane, but wait to open files until after frame
		// initialization
		tabbedPane_ = new TabbedPane(getAttributes());
		enableDragAndDrop(tabbedPane_);

		// initialize frame
		initFrame(jMenuBar);

		// This is an ugly hack. It seems like JFrame.setLocation() is buggy
		// on Solaris jdk versions before 1.4
		if (HAS_SOLARIS_BUG) {
			frame_.setLocation(50, 50);
		} else {
			frame_.setLocation(getAttributes().getX(), getAttributes().getY());
		}
		frame_.addWindowListener(new WindowTracker(getAttributes()));
		frame_.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (tabbedPane_.getTabCount() > 0) {
					getAttributes().setSelectedTabIndex(tabbedPane_.getSelectedIndex());
				}
				((Window) e.getSource()).dispose();
			}

			public void windowClosed(WindowEvent e) {
				try {
					getAttributes().store();
				} catch (IOException ioe) {
					System.err.println("Error encountered while storing properties...");
					ioe.printStackTrace(System.err);
				} finally {
					systemInterface_.exit(0);
				}
			}
		});
		enableDragAndDrop(frame_);

		// Open files from attributes; this is done after the frame is complete
		// and all components have been added to it to make sure that the frame
		// can be shown absolutely as soon as possible. If we put this code
		// before frame creation (as in v1.0), frame creation may take longer
		// because there are more threads (spawned in the course of open())
		// contending for processor time.
		File[] files = getAttributes().getFollowedFiles();
		StringBuffer nonexistentFilesBuffer = null;
		int nonexistentFileCount = 0;
		File file;
		for (int i = 0; i < files.length; i++) {
			file = files[i];
			try {
				open(file, false);
			}
			catch (FileNotFoundException e) {
				// This file has been deleted since the previous execution.
				// Remove it from the list of followed files
				getAttributes().removeFollowedFile(file);
				nonexistentFileCount++;
				if (nonexistentFilesBuffer == null) {
					nonexistentFilesBuffer = new StringBuffer(file.getAbsolutePath());
				} else {
					nonexistentFilesBuffer.append(file.getAbsolutePath());
				}
				nonexistentFilesBuffer.append(MESSAGE_LINE_SEPARATOR);
			}
		}
		if (nonexistentFileCount > 0) {
			// Alert the user of the fact that one or more files have been
			// deleted since the previous execution
			String message = MessageFormat.format(getResourceBundle().getString(
					"message.filesDeletedSinceLastExecution.text"), new Object[] {
					new Long(nonexistentFileCount), nonexistentFilesBuffer.toString() });
			JOptionPane.showMessageDialog(frame_, message, getResourceBundle().getString(
					"message.filesDeletedSinceLastExecution.title"), JOptionPane.WARNING_MESSAGE);
		}
		if (tabbedPane_.getTabCount() > 0) {
			if (tabbedPane_.getTabCount() > getAttributes().getSelectedTabIndex()) {
				tabbedPane_.setSelectedIndex(getAttributes().getSelectedTabIndex());
			} else {
				tabbedPane_.setSelectedIndex(0);
			}
		} else {
			getAction(Close.NAME).setEnabled(false);
			getAction(Reload.NAME).setEnabled(false);
			getAction(Edit.NAME).setEnabled(false);
			getAction(Top.NAME).setEnabled(false);
			getAction(Bottom.NAME).setEnabled(false);
			getAction(Clear.NAME).setEnabled(false);
			getAction(ClearAll.NAME).setEnabled(false);
			getAction(Delete.NAME).setEnabled(false);
			getAction(DeleteAll.NAME).setEnabled(false);
			getAction(Pause.NAME).setEnabled(false);
		}
	}

	/**
	 * @param jMenuBar
	 */
	private void initFrame(JMenuBar jMenuBar) {
		frame_.setJMenuBar(jMenuBar);
		frame_.getContentPane().add(toolBar_, BorderLayout.NORTH);
		frame_.getContentPane().add(tabbedPane_, BorderLayout.CENTER);
		frame_.setSize(getAttributes().getWidth(), getAttributes().getHeight());
	}

	/**
	 * Builds the menu bar for the application
	 * 
	 * @return reference the constructed menu bar
	 */
	private JMenuBar buildMenuBar() {
		// file menu
		Menu fileMenu = new Menu(getResourceBundle().getString("menu.File.name"),
				getResourceBundle().getString("menu.File.mnemonic"));
		fileMenu.addFollowAppAction(getAction(Open.NAME));
		fileMenu.addFollowAppAction(getAction(Close.NAME));
		fileMenu.addFollowAppAction(getAction(Reload.NAME));
		fileMenu.addSeparator();
		fileMenu.addFollowAppAction(getAction(Reset.NAME));
		fileMenu.addFollowAppAction(getAction(Pause.NAME));
		fileMenu.addSeparator();
		recentFilesMenu_ = new Menu(getResourceBundle().getString("menu.RecentFiles.name"),
				getResourceBundle().getString("menu.RecentFiles.mnemonic"));
		refreshRecentFilesMenu();
		fileMenu.add(recentFilesMenu_);
		fileMenu.addSeparator();
		fileMenu.addFollowAppAction(getAction(Exit.NAME));
		// edit menu
		Menu editMenu = new Menu(getResourceBundle().getString("menu.Edit.name"),
				getResourceBundle().getString("menu.Edit.mnemonic"));
		editMenu.addFollowAppAction(getAction(Find.NAME));
		editMenu.addSeparator();
		editMenu.addFollowAppAction(getAction(ClearHighlights.NAME));
		editMenu.addFollowAppAction(getAction(ClearAllHighlights.NAME));
		// tool menu
		Menu toolsMenu = new Menu(getResourceBundle().getString("menu.Tools.name"),
				getResourceBundle().getString("menu.Tools.mnemonic"));
		toolsMenu.addFollowAppAction(getAction(Top.NAME));
		toolsMenu.addFollowAppAction(getAction(Bottom.NAME));
		toolsMenu.addSeparator();
		toolsMenu.addFollowAppAction(getAction(Clear.NAME));
		toolsMenu.addFollowAppAction(getAction(ClearAll.NAME));
		toolsMenu.addFollowAppAction(getAction(Delete.NAME));
		toolsMenu.addFollowAppAction(getAction(DeleteAll.NAME));
		toolsMenu.addSeparator();
		toolsMenu.addFollowAppAction(getAction(Configure.NAME));
		toolsMenu.addFollowAppAction(getAction(Edit.NAME));
		// window menu
		Menu windowMenu = new Menu(getResourceBundle().getString("menu.Window.name"),
				getResourceBundle().getString("menu.Window.mnemonic"));
		windowMenu.addFollowAppAction(getAction(NextTab.NAME));
		windowMenu.addFollowAppAction(getAction(PreviousTab.NAME));
		// help menu
		Menu helpMenu = new Menu(getResourceBundle().getString("menu.Help.name"),
				getResourceBundle().getString("menu.Help.mnemonic"));
		helpMenu.addFollowAppAction(getAction(About.NAME));
		if (DEBUG) {
			helpMenu.addSeparator();
			helpMenu.addFollowAppAction(getAction(Debug.NAME));
		}

		// create menu bar and add menus
		JMenuBar jMenuBar = new JMenuBar();
		jMenuBar.add(fileMenu);
		jMenuBar.add(editMenu);
		jMenuBar.add(toolsMenu);
		jMenuBar.add(windowMenu);
		jMenuBar.add(helpMenu);
		return jMenuBar;
	}

	public void refreshRecentFilesMenu() {
		if (recentFilesMenu_ != null) {
			recentFilesMenu_.removeAll();
			File[] recentFiles = getAttributes().getRecentFiles();
			// descend down the list to order files by last opened
			for (int i = recentFiles.length - 1; i >= 0; i--) {
				recentFilesMenu_.add(new Open(this, recentFiles[i]));
			}
		}
	}

	/**
	 * Builds the popup menu shown when right clicking in a text area.
	 */
	private void buildPopupMenu() {
		popupMenu_ = new PopupMenu();
		popupMenu_.addFollowAppAction(getAction(Open.NAME));
		popupMenu_.addFollowAppAction(getAction(Close.NAME));
		popupMenu_.addFollowAppAction(getAction(Reload.NAME));
		popupMenu_.addSeparator();
		popupMenu_.addFollowAppAction(getAction(Top.NAME));
		popupMenu_.addFollowAppAction(getAction(Bottom.NAME));
		popupMenu_.addSeparator();
		popupMenu_.addFollowAppAction(getAction(Clear.NAME));
		popupMenu_.addFollowAppAction(getAction(ClearAll.NAME));
		popupMenu_.addFollowAppAction(getAction(Delete.NAME));
		popupMenu_.addFollowAppAction(getAction(DeleteAll.NAME));
		popupMenu_.addSeparator();
		popupMenu_.addFollowAppAction(getAction(Configure.NAME));
		popupMenu_.addFollowAppAction(getAction(Edit.NAME));
	}

	/**
	 * Builds the toolbar shown at the top of the application
	 */
	private void buildToolBar() {
		toolBar_ = new ToolBar();
		toolBar_.addFollowAppAction(getAction(Open.NAME));
		toolBar_.addSeparator();
		toolBar_.addFollowAppAction(getAction(Top.NAME));
		toolBar_.addFollowAppAction(getAction(Bottom.NAME));
		toolBar_.addSeparator();
		toolBar_.addFollowAppAction(getAction(Clear.NAME));
		toolBar_.addFollowAppAction(getAction(ClearAll.NAME));
		toolBar_.addFollowAppAction(getAction(Delete.NAME));
		toolBar_.addFollowAppAction(getAction(DeleteAll.NAME));
		toolBar_.addSeparator();
		toolBar_.addFollowAppAction(getAction(Reset.NAME));
		toolBar_.addFollowAppAction(getAction(Pause.NAME));
		toolBar_.addSeparator();
		toolBar_.addFollowAppAction(getAction(Configure.NAME));
	}

	private StartupStatus startupStatus_;

	public void show() {
		frame_.setVisible(true);
	}

	/**
	 * Warning: This method should be called only from (1) the FollowApp
	 * initializer (before any components are realized) or (2) from the event
	 * dispatching thread.
	 */
	void open(File file, boolean addFileToAttributes, boolean startFollowing)
			throws FileNotFoundException {
		if (file == null || !file.exists()) {
			throw new FileNotFoundException(file.getName() + " not found.");
		}
		FileFollowingPane fileFollowingPane = (FileFollowingPane) fileToFollowingPaneMap_.get(file);
		if (fileFollowingPane != null) {
			// File is already open; merely select its tab
			tabbedPane_.setSelectedComponent(fileFollowingPane);
		} else {
			fileFollowingPane = new FileFollowingPane(file, getAttributes().getBufferSize(),
					getAttributes().getLatency(), getAttributes().autoScroll(), getAttributes()
							.getFont(), getAttributes().getTabSize());
			SearchableTextPane ffpTextPane = fileFollowingPane.getTextPane();
			enableDragAndDrop(ffpTextPane);
			fileFollowingPane.setSize(frame_.getSize());
			ffpTextPane.setFont(getAttributes().getFont());
			ffpTextPane.addMouseListener(getRightClickListener());
			fileToFollowingPaneMap_.put(file, fileFollowingPane);
			if (startFollowing) {
				fileFollowingPane.startFollowing();
			}
			tabbedPane_.addTab(file.getName(), null, fileFollowingPane, file.getAbsolutePath());
			int tabCount = tabbedPane_.getTabCount();
			if (tabCount < 10) {
				// KeyEvent.VK_1 through KeyEvent.VK_9 is represented by the
				// ascii characters 1-9 (49-57)
				int index = tabCount - 1;
				tabbedPane_.setMnemonicAt(index, index + ((int) '1'));
			}
			tabbedPane_.setSelectedIndex(tabCount - 1);
			// add a listener to set the pause icon correctly
			fileFollowingPane.addComponentListener(new ComponentAdapter() {
				public void componentShown(ComponentEvent e) {
					FileFollowingPane ffp = (FileFollowingPane) e.getSource();
					Pause pause = (Pause) getAction(Pause.NAME);
					pause.setIconByState(ffp.isFollowing());
				}
			});
			if (!getAction(Close.NAME).isEnabled()) {
				getAction(Close.NAME).setEnabled(true);
				getAction(Reload.NAME).setEnabled(true);
				getAction(Edit.NAME).setEnabled(true);
				getAction(Top.NAME).setEnabled(true);
				getAction(Bottom.NAME).setEnabled(true);
				getAction(Clear.NAME).setEnabled(true);
				getAction(ClearAll.NAME).setEnabled(true);
				getAction(Delete.NAME).setEnabled(true);
				getAction(DeleteAll.NAME).setEnabled(true);
				getAction(Pause.NAME).setEnabled(true);
			}
			if (addFileToAttributes) {
				getAttributes().addFollowedFile(file);
				getAttributes().addRecentFile(file);
				refreshRecentFilesMenu();
			}
		}
	}

	void open(File file, boolean addFileToAttributes) throws FileNotFoundException {
		open(file, addFileToAttributes, getAttributes().autoScroll());
	}

	/**
	 * Warning: This method should be called only from the event dispatching
	 * thread.
	 * 
	 * @param cursorType
	 *            may be Cursor.DEFAULT_CURSOR or Cursor.WAIT_CURSOR
	 */
	public void setCursor(int cursorType) {
		if (cursorType == currentCursor_) {
			return;
		}
		switch (cursorType) {
		case Cursor.DEFAULT_CURSOR:
			if (defaultCursor_ == null) {
				defaultCursor_ = Cursor.getDefaultCursor();
			}
			frame_.setCursor(defaultCursor_);
			break;

		case Cursor.WAIT_CURSOR:
			if (waitCursor_ == null) {
				waitCursor_ = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
			}
			frame_.setCursor(waitCursor_);
			break;

		default:
			throw new IllegalArgumentException(
					"Supported cursors are Cursor.DEFAULT_CURSOR and Cursor.WAIT_CURSOR");
		}
		currentCursor_ = cursorType;
	}

	// Lazy initializer for the right-click listener which invokes a popup menu
	private MouseListener getRightClickListener() {
		if (rightClickListener_ == null) {
			rightClickListener_ = new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						Component source = e.getComponent();
						popupMenu_.show(source, e.getX(), e.getY());
					}
				}
			};
		}
		return rightClickListener_;
	}

	public void enableDragAndDrop(Component c) {
		// Invoking this constructor automatically sets the component's drop
		// target
		new DropTarget(c, new DndFileOpener(this));
	}

	public void disableDragAndDrop(Component c) {
		c.setDropTarget(null);
	}

	public FileFollowingPane getSelectedFileFollowingPane() {
		return (FileFollowingPane) tabbedPane_.getSelectedComponent();
	}

	public List getAllFileFollowingPanes() {
		int tabCount = tabbedPane_.getTabCount();
		List allFileFollowingPanes = new ArrayList();
		for (int i = 0; i < tabCount; i++) {
			allFileFollowingPanes.add(tabbedPane_.getComponentAt(i));
		}
		return allFileFollowingPanes;
	}

	public FollowAppAttributes getAttributes() {
		return attributes_;
	}

	public Map getFileToFollowingPaneMap() {
		return fileToFollowingPaneMap_;
	}

	public JFrame getFrame() {
		return frame_;
	}

	public static FollowApp getInstance() {
		return instance_;
	}

	public ResourceBundle getResourceBundle() {
		return resBundle_;
	}

	public SystemInterface getSystemInterface() {
		return systemInterface_;
	}

	public void setSystemInterface(SystemInterface systemInterface) {
		this.systemInterface_ = systemInterface;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane_;
	}

	// We should remove this hack once JDK 1.4 gets wide adoption on Solaris.
	static {
		boolean isSolaris = "SunOS".equals(System.getProperty("os.name"));

		if (isSolaris) {
			String version = System.getProperty("java.version");
			if ((version != null) && version.startsWith("1.")) {
				String substring = version.substring(2, 3);
				try {
					int minor = Integer.parseInt(substring);
					if (minor < 4) {
						HAS_SOLARIS_BUG = true;
					}
				} catch (NumberFormatException nfe) {
					// Nothing else to do.
				}
			}
		}
	}

	static void centerWindowInScreen(Window window) {
		Dimension screenSize = window.getToolkit().getScreenSize();
		Dimension windowSize = window.getPreferredSize();
		window.setLocation((int) (screenSize.getWidth() / 2 - windowSize.getWidth() / 2),
				(int) (screenSize.getHeight() / 2 - windowSize.getHeight() / 2));
	}

	/**
	 * Invoke this method to start the Follow application. If any command-line
	 * arguments are passed in, they are assume to be filenames and are opened
	 * in the Follow application
	 * 
	 * @param args
	 *            files to be opened
	 */
	public static void main(String[] args) {
		try {
			instance_ = new FollowApp(args);
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					// ensure all widgets inited before opening files
					instance_.show();
					instance_.startupStatus_.markDone(instance_.startupStatus_.CREATE_WIDGETS);
				}
			});
			instance_.startupStatus_.dispose();
			// commented code below so that windows follow based on setting in
			// preferences which is set on the pane when the file is opened
			// for (int i=0; i < instance_.tabbedPane_.getTabCount(); i++) {
			// ((FileFollowingPane)instance_.tabbedPane_.getComponentAt(i)).startFollowing();
			// }
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
	}
}