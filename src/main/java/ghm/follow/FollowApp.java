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

package ghm.follow;

import ghm.follow.config.Configure;
import ghm.follow.config.FollowAppAttributes;
import ghm.follow.event.WindowTracker;
import ghm.follow.gui.About;
import ghm.follow.gui.Clear;
import ghm.follow.gui.ClearAll;
import ghm.follow.gui.Close;
import ghm.follow.gui.Debug;
import ghm.follow.gui.Delete;
import ghm.follow.gui.DeleteAll;
import ghm.follow.gui.DndFileOpener;
import ghm.follow.gui.Edit;
import ghm.follow.gui.Exit;
import ghm.follow.gui.FileFollowingPane;
import ghm.follow.gui.FollowAppAction;
import ghm.follow.gui.Menu;
import ghm.follow.gui.ComponentBuilder;
import ghm.follow.gui.Open;
import ghm.follow.gui.Pause;
import ghm.follow.gui.PopupMenu;
import ghm.follow.gui.Reload;
import ghm.follow.gui.Reset;
import ghm.follow.gui.StartupStatus;
import ghm.follow.gui.TabbedPane;
import ghm.follow.gui.ToolBar;
import ghm.follow.gui.FollowAppAction.ActionContext;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class' main() method is the entry point into the Follow application.
 * 
 * @see #main(String[])
 * @author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
public class FollowApp
{
	private static Logger log = LoggerFactory.getLogger(FollowApp.class);
	private int currentCursor_ = Cursor.DEFAULT_CURSOR;
	private Cursor defaultCursor_;
	private Cursor waitCursor_;
	private Map<File, FileFollowingPane> fileToFollowingPaneMap_ = new HashMap<File, FileFollowingPane>();
	private JTabbedPane tabbedPane_;
	private ToolBar toolBar_;
	private PopupMenu popupMenu_;
	private Menu recentFilesMenu_;
	private MouseListener rightClickListener_;
	private HashMap<String, FollowAppAction> actions_ = new HashMap<String, FollowAppAction>();
	private SystemInterface systemInterface_;
	private StartupStatus startupStatus_;

	private FollowAppAttributes attributes_;
	private static FollowApp instance_;
	private static ResourceBundle resBundle_ = ResourceBundle
			.getBundle("ghm.follow.FollowAppResourceBundle");
	private JFrame frame_;

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String MESSAGE_LINE_SEPARATOR = "\n";
	public static final boolean DEBUG = Boolean.getBoolean("follow.debug");
	public static boolean HAS_SOLARIS_BUG = false;

	// We should remove this hack once JDK 1.4 gets wide adoption on Solaris.
	static
	{
		boolean isSolaris = "SunOS".equals(System.getProperty("os.name"));

		if (isSolaris)
		{
			String version = System.getProperty("java.version");
			if ((version != null) && version.startsWith("1."))
			{
				String substring = version.substring(2, 3);
				try
				{
					int minor = Integer.parseInt(substring);
					if (minor < 4)
					{
						HAS_SOLARIS_BUG = true;
					}
				}
				catch (NumberFormatException nfe)
				{
					// Nothing else to do.
				}
			}
		}
	}

	/**
	 * @param fileNames
	 *            names of files to be opened
	 */
	FollowApp(List<String> fileNames) throws IOException, InterruptedException,
			InvocationTargetException
	{
		this(fileNames, null);
	}

	FollowApp(List<String> fileNames, File propertyFile) throws IOException, InterruptedException,
			InvocationTargetException
	{
		// Create & show startup status window
		startupStatus_ = new StartupStatus(resBundle_);
		centerWindowInScreen(startupStatus_);
		startupStatus_.pack();
		SwingUtilities.invokeAndWait(new Runnable()
		{
			public void run()
			{
				startupStatus_.setVisible(true);
			}
		});

		// Ghastly workaround for bug in Font construction, in review by
		// Sun with review id 108683.
		GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		SwingUtilities.invokeAndWait(new Runnable()
		{
			public void run()
			{
				startupStatus_.markDone(startupStatus_.LOAD_SYSTEM_FONTS);
			}
		});

		// create frame first. the close operation is handled in WindowTracker
		frame_ = new JFrame(getResourceString("frame.title"));

		// load the attributes
		attributes_ = new FollowAppAttributes(propertyFile);

		// add listeners to update the recent files list
		RecentFileListener rfl = new RecentFileListener();
		attributes_.addPropertyChangeListener(FollowAppAttributes.RECENT_FILES_KEY, rfl);
		attributes_.addPropertyChangeListener(FollowAppAttributes.RECENT_FILES_MAX_KEY, rfl);

		// load the actions referenced in the application
		loadActions();

		// initialize SystemInterface
		systemInterface_ = new DefaultSystemInterface(this);

		// initialize menubar
		JMenuBar jMenuBar = ComponentBuilder.buildMenuBar(resBundle_, getActions());

		// set the recent files menu to local variable so it can be updated easily
		recentFilesMenu_ = ComponentBuilder.recentFilesMenu;

		// fake an event to get the menu setup initially
		rfl.propertyChange(null);

		// initialize popupMenu
		popupMenu_ = ComponentBuilder.buildPopupMenu(getActions());

		// initialize toolbar
		toolBar_ = ComponentBuilder.buildToolBar(getActions());

		// initialize tabbedPane, but wait to open files until after frame
		// initialization
		tabbedPane_ = new TabbedPane(attributes_);
		enableDragAndDrop(tabbedPane_);

		// initialize frame
		initFrame(jMenuBar);

		// This is an ugly hack. It seems like JFrame.setLocation() is buggy
		// on Solaris jdk versions before 1.4
		if (HAS_SOLARIS_BUG)
		{
			frame_.setLocation(50, 50);
		}
		else
		{
			frame_.setLocation(attributes_.getX(), attributes_.getY());
		}

		// track window close events. WindowTracker handles the close operation
		frame_.addWindowListener(new WindowTracker(attributes_, tabbedPane_, systemInterface_));
		enableDragAndDrop(frame_);

		// Open files from attributes; this is done after the frame is complete
		// and all components have been added to it to make sure that the frame
		// can be shown absolutely as soon as possible. If we put this code
		// before frame creation (as in v1.0), frame creation may take longer
		// because there are more threads (spawned in the course of open())
		// contending for processor time.
		List<File> files = attributes_.getFollowedFiles();
		StringBuffer nonexistentFilesBuffer = null;
		int nonexistentFileCount = 0;
		for (File file : files)
		{
			try
			{
				openFile(file, true);
			}
			catch (FileNotFoundException e)
			{
				// This file has been deleted since the previous execution.
				// Remove it from the list of followed files
				attributes_.removeFollowedFile(file);
				nonexistentFileCount++;
				if (nonexistentFilesBuffer == null)
				{
					nonexistentFilesBuffer = new StringBuffer(file.getAbsolutePath());
				}
				else
				{
					nonexistentFilesBuffer.append(file.getAbsolutePath());
				}
				nonexistentFilesBuffer.append(MESSAGE_LINE_SEPARATOR);
			}
		}

		// open files from the command line
		for (String fileName : fileNames)
		{
			File file = new File(fileName);
			if (!file.exists())
			{
				String msg = MessageFormat.format(
						getResourceString("message.cmdLineFileNotFound.text"),
						new Object[] { file });
				log.info(msg);
			}
			else if (!attributes_.followedFileListContains(file))
			{
				openFile(file, true);
//				attributes_.addFollowedFile(file);
			}
		}

		if (nonexistentFileCount > 0)
		{
			// Alert the user of the fact that one or more files have been
			// deleted since the previous execution
			String text = getResourceString("message.filesDeletedSinceLastExecution.text");
			String message = MessageFormat.format(text, new Object[] { nonexistentFileCount,
					nonexistentFilesBuffer.toString() });
//			String title = getResourceString("message.filesDeletedSinceLastExecution.title");
//			JOptionPane.showMessageDialog(frame_, message, title, JOptionPane.WARNING_MESSAGE);
			log.info(message);
		}

		int tabCount = tabbedPane_.getTabCount();
		if (tabCount > 0)
		{
			if (tabCount > attributes_.getSelectedTabIndex())
			{
				tabbedPane_.setSelectedIndex(attributes_.getSelectedTabIndex());
			}
			else
			{
				tabbedPane_.setSelectedIndex(0);
			}
		}
	}

	/**
	 * Close the current tab
	 */
	public void closeFile()
	{
		FileFollowingPane fileFollowingPane = getSelectedFileFollowingPane();
		int tab = tabbedPane_.getSelectedIndex();
		if (tab >= 0)
		{
			tabbedPane_.removeTabAt(tab);
			disableDragAndDrop(fileFollowingPane.getTextPane());
			attributes_.removeFollowedFile(fileFollowingPane.getFollowedFile());
			fileFollowingPane.stopFollowing();
			fileToFollowingPaneMap_.remove(fileFollowingPane.getFollowedFile());
		}
		updateActions();
	}

	/**
	 * Get a string from the resource bundle. Convenience method to shorten and centralize this
	 * common call
	 * 
	 * @param key
	 * @return The value of key in the resource bundle. null if the key is not found.
	 */
	public static String getResourceString(String key)
	{
		String value = null;
		try
		{
			value = resBundle_.getString(key);
		}
		catch (MissingResourceException mre)
		{
			log.warn(mre.getMessage());
		}
		return value;
	}

	/**
	 * Gets an image icon from the resource path.
	 * 
	 * @param clazz
	 *            The class to use as an entry point to the resource path. Image path should be
	 *            relative to this class.
	 * @param iconNameKey
	 *            The resource key name where the image is defined.
	 * @return An image icon based on the URL generated from the value of iconNameKey. null if no
	 *         URL can be found.
	 */
	public static ImageIcon getIcon(Class<?> clazz, String iconNameKey)
	{
		String filename = getResourceString(iconNameKey);
		URL url = clazz.getResource(filename);
		log.debug("Class: {}, iconNameKey: {}", clazz, iconNameKey);
		log.debug("filename: {}", filename);
		log.debug("url: {}", url);
		ImageIcon icon = null;
		if (url != null)
		{
			icon = new ImageIcon(url);
			log.debug("errored: {}", (java.awt.MediaTracker.ERRORED == icon.getImageLoadStatus()));
		}
		return icon;
	}

	/**
	 * Loads the actions used in the application
	 * 
	 * @throws IOException
	 */
	private void loadActions() throws IOException
	{
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
		if (DEBUG)
		{
			putAction(Debug.NAME, new Debug(this));
		}
		putAction(Pause.NAME, new Pause(this));
		putAction(NextTab.NAME, new NextTab(this));
		putAction(PreviousTab.NAME, new PreviousTab(this));
		putAction(Find.NAME, new Find(this));
		putAction(ClearHighlights.NAME, new ClearHighlights(this));
		putAction(ClearAllHighlights.NAME, new ClearAllHighlights(this));
		putAction(Reset.NAME, new Reset(this));
	}

	/**
	 * @param jMenuBar
	 */
	private void initFrame(JMenuBar jMenuBar)
	{
		frame_.setJMenuBar(jMenuBar);
		frame_.getContentPane().add(toolBar_, BorderLayout.NORTH);
		frame_.getContentPane().add(tabbedPane_, BorderLayout.CENTER);
		frame_.setSize(attributes_.getWidth(), attributes_.getHeight());
	}

	public void show()
	{
		frame_.setVisible(true);
	}

	public FollowAppAction getAction(String name)
	{
		return actions_.get(name);
	}

	/**
	 * Get all actions associated to the application
	 * 
	 * @return
	 */
	public HashMap<String, FollowAppAction> getActions()
	{
		return actions_;
	}

	/**
	 * Set an action to the action map of the application.
	 * 
	 * @param name
	 *            The key to set the action to.
	 * @param action
	 *            The action to create an association for.
	 */
	public void putAction(String name, FollowAppAction action)
	{
		if (FollowAppAction.ActionContext.APP == action.getContext())
			action.setEnabled(true);
		else
			action.setEnabled(false);
		actions_.put(name, action);
	}

	public void openFile(File file) throws FileNotFoundException
	{
		openFile(file, attributes_.autoScroll());
	}

	/**
	 * Warning: This method should be called only from (1) the FollowApp initializer (before any
	 * components are realized) or (2) from the event dispatching thread.
	 */
	void openFile(File file, boolean startFollowing) throws FileNotFoundException
	{
		if (file == null)
		{
			throw new FileNotFoundException("file is null.");
		}
		if (!file.exists())
		{
			throw new FileNotFoundException(file.getName() + " not found.");
		}
		FileFollowingPane fileFollowingPane = (FileFollowingPane) fileToFollowingPaneMap_.get(file);
		if (fileFollowingPane != null)
		{
			// File is already open; merely select its tab
			tabbedPane_.setSelectedComponent(fileFollowingPane);
		}
		else
		{
			fileFollowingPane = new FileFollowingPane(file, attributes_.getBufferSize(),
					attributes_.getLatency(), attributes_.autoScroll(), attributes_.getFont(),
					attributes_.getTabSize());
			SearchableTextPane ffpTextPane = fileFollowingPane.getTextPane();
			enableDragAndDrop(ffpTextPane);
			fileFollowingPane.setSize(frame_.getSize());
			ffpTextPane.setFont(attributes_.getFont());
			ffpTextPane.addMouseListener(getRightClickListener());
			fileToFollowingPaneMap_.put(file, fileFollowingPane);
			if (startFollowing)
			{
				fileFollowingPane.startFollowing();
			}
			tabbedPane_.addTab(file.getName(), null, fileFollowingPane, file.getAbsolutePath());
			int tabCount = tabbedPane_.getTabCount();
			if (tabCount < 10)
			{
				// KeyEvent.VK_1 through KeyEvent.VK_9 is represented by the
				// ascii characters 1-9 (49-57)
				int index = tabCount - 1;
				tabbedPane_.setMnemonicAt(index, index + ((int) '1'));
			}
			tabbedPane_.setSelectedIndex(tabCount - 1);
			// add a listener to set the pause icon correctly
			fileFollowingPane.addComponentListener(new ComponentAdapter()
			{
				public void componentShown(ComponentEvent e)
				{
					FileFollowingPane ffp = (FileFollowingPane) e.getSource();
					Pause pause = (Pause) getAction(Pause.NAME);
					pause.setIconByState(ffp.isFollowingPaused());
				}
			});

			// add the file to history
			attributes_.addFollowedFile(file);
			attributes_.addRecentFile(file);

			updateActions();
		}
	}

	private void updateActions()
	{
		int tabCount = tabbedPane_.getTabCount();
		for (FollowAppAction a : actions_.values())
		{
			if (tabCount <= 1 && a.getContext() == ActionContext.MULTI_FILE)
				a.setEnabled(false);
			else if (tabCount == 0 && a.getContext() == ActionContext.SINGLE_FILE)
				a.setEnabled(false);
			else
				a.setEnabled(true);
		}
	}

	/**
	 * Warning: This method should be called only from the event dispatching thread.
	 * 
	 * @param cursorType
	 *            may be Cursor.DEFAULT_CURSOR or Cursor.WAIT_CURSOR
	 */
	public void setCursor(int cursorType)
	{
		if (cursorType == currentCursor_)
		{
			return;
		}
		switch (cursorType)
		{
			case Cursor.DEFAULT_CURSOR:
				if (defaultCursor_ == null)
				{
					defaultCursor_ = Cursor.getDefaultCursor();
				}
				frame_.setCursor(defaultCursor_);
				break;

			case Cursor.WAIT_CURSOR:
				if (waitCursor_ == null)
				{
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
	private MouseListener getRightClickListener()
	{
		if (rightClickListener_ == null)
		{
			rightClickListener_ = new MouseAdapter()
			{
				public void mouseReleased(MouseEvent e)
				{
					if (SwingUtilities.isRightMouseButton(e))
					{
						Component source = e.getComponent();
						popupMenu_.show(source, e.getX(), e.getY());
					}
				}
			};
		}
		return rightClickListener_;
	}

	public void enableDragAndDrop(Component c)
	{
		// Invoking this constructor automatically sets the component's drop target
		new DropTarget(c, new DndFileOpener(this));
	}

	public void disableDragAndDrop(Component c)
	{
		c.setDropTarget(null);
	}

	public FileFollowingPane getSelectedFileFollowingPane()
	{
		return (FileFollowingPane) tabbedPane_.getSelectedComponent();
	}

	public List<FileFollowingPane> getAllFileFollowingPanes()
	{
		int tabCount = tabbedPane_.getTabCount();
		List<FileFollowingPane> allFileFollowingPanes = new ArrayList<FileFollowingPane>();
		for (int i = 0; i < tabCount; i++)
		{
			allFileFollowingPanes.add((FileFollowingPane) tabbedPane_.getComponentAt(i));
		}
		return allFileFollowingPanes;
	}

	public FollowAppAttributes getAttributes()
	{
		return attributes_;
	}

	public Map<File, FileFollowingPane> getFileToFollowingPaneMap()
	{
		return fileToFollowingPaneMap_;
	}

	public JFrame getFrame()
	{
		return frame_;
	}

	public static FollowApp getInstance()
	{
		return instance_;
	}

	public SystemInterface getSystemInterface()
	{
		return systemInterface_;
	}

	public void setSystemInterface(SystemInterface systemInterface)
	{
		this.systemInterface_ = systemInterface;
	}

	public JTabbedPane getTabbedPane()
	{
		return tabbedPane_;
	}

	public static void centerWindowInScreen(Window window)
	{
		Dimension screenSize = window.getToolkit().getScreenSize();
		Dimension windowSize = window.getPreferredSize();
		window.setLocation((int) (screenSize.getWidth() / 2 - windowSize.getWidth() / 2),
				(int) (screenSize.getHeight() / 2 - windowSize.getHeight() / 2));
	}

	/**
	 * Invoke this method to start the Follow application. If any command-line arguments are passed
	 * in, they are assume to be filenames and are opened in the Follow application
	 * 
	 * @param args
	 *            files to be opened
	 */
	public static void main(String[] args)
	{
		try
		{
			ArrayList<String> fileNames = new ArrayList<String>();
			File propFile = null;
			for (int i = 0; i < args.length; i++)
			{
				if (args[i].startsWith("-"))
				{
					if ("-propFile".equalsIgnoreCase(args[i]))
					{
						propFile = new File(args[++i]);
					}
				}
				else
				{
					fileNames.add(args[i]);
				}
			}
			instance_ = new FollowApp(fileNames, propFile);
			SwingUtilities.invokeAndWait(new Runnable()
			{
				public void run()
				{
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
		}
		catch (Throwable t)
		{
			log.error("Unhandled exception", t);
			System.exit(-1);
		}
	}

	private class RecentFileListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (recentFilesMenu_ != null)
			{
				recentFilesMenu_.removeAll();
				List<File> recentFiles = attributes_.getRecentFiles();
				// descend down the list to order files by last opened
				for (int i = recentFiles.size() - 1; i >= 0; i--)
				{
					// have to use FollowApp.this because 'this' is now the context of
					// the inner class
					recentFilesMenu_.add(new Open(FollowApp.this, recentFiles.get(i)));
				}
			}
		}
	}
}