/* 
Copyright (C) 2000-2003 Greg Merrill (greghmerrill@yahoo.com)

This file is part of Follow (http://follow.sf.net).

Follow is free software; you can redistribute it and/or modify
it under the terms of version 2 of the GNU General Public
License as published by the Free Software Foundation.

Follow is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Follow; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package ghm.followgui;

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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
This class' main() method is the entry point into the Follow 
application.
@see #main(String[])
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
public class FollowApp {

  /**
   * @param fileNames names of files to be opened
   */
  FollowApp (String[] fileNames) 
    throws IOException, InterruptedException, InvocationTargetException {
    // Create & show startup status window
    startupStatus_ = new StartupStatus(resBundle_);
    centerWindowInScreen(startupStatus_);
    startupStatus_.pack();
    SwingUtilities.invokeAndWait(new Runnable() { public void run () {
      startupStatus_.show();
    }});

    // Ghastly workaround for bug in Font construction, in review by
    // Sun with review id 108683.
GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    SwingUtilities.invokeAndWait(new Runnable() { public void run () {
      startupStatus_.markDone(startupStatus_.LOAD_SYSTEM_FONTS);
    }});

    // create frame first
    frame_ = new JFrame(resBundle_.getString("frame.title"));

    // initialize attributes
    attributes_ = new FollowAppAttributes(this);
    for (int i=0; i < fileNames.length; i++) {
      File file = new File(fileNames[i]);
      if (!file.exists()) {
        String msg = MessageFormat.format(
          resBundle_.getString("message.cmdLineFileNotFound.text"),
          new Object[]{file}
          );
        System.out.println(msg);
      }
      else if (!attributes_.followedFileListContains(file)) {
        attributes_.addFollowedFile(file);
      }
    }

    // initialize actions
    open_ = new Open(this);
    close_ = new Close(this);
    reload_ = new Reload(this);
    edit_ = new Edit(this);
    exit_ = new Exit(this);
    top_ = new Top(this);
    bottom_ = new Bottom(this);
    clear_ = new Clear(this);
    clearAll_ = new ClearAll(this);
    delete_ = new Delete(this);
    deleteAll_ = new DeleteAll(this);
    configure_ = new Configure(this);
    about_ = new About(this);
    if (DEBUG) {
        debug_ = new Debug(this);
    }
    pause_ = new Pause(this);
    nextTab_ = new NextTab(this);
    prevTab_ = new PreviousTab(this);
    find_ = new Find(this);
    clearHighlights_ = new ClearHighlights(this);
    clearAllHighlights_ = new ClearAllHighlights(this);

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
    tabbedPane_ = new TabbedPane(attributes_);
    enableDragAndDrop(tabbedPane_);

    // initialize frame
    initFrame(jMenuBar);

    // This is an ugly hack.  It seems like JFrame.setLocation() is buggy
    // on Solaris jdk versions before 1.4
    if (HAS_SOLARIS_BUG) {
        frame_.setLocation(50, 50);
    } else {
        frame_.setLocation(attributes_.getX(), attributes_.getY());
    }
    frame_.addWindowListener(new WindowTracker(attributes_));
    frame_.addWindowListener(new WindowAdapter () {
      public void windowClosing (WindowEvent e) {
        if (tabbedPane_.getTabCount() > 0) {
          attributes_.setSelectedTabIndex(tabbedPane_.getSelectedIndex());
        }
        ((Window)e.getSource()).dispose();
      }
      public void windowClosed (WindowEvent e) {
        try {
          attributes_.store();
        }
        catch (IOException ioe) {
          System.err.println("Error encountered while storing properties...");
          ioe.printStackTrace(System.err);
        }
        finally {
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
    Iterator i = attributes_.getFollowedFiles();
    StringBuffer nonexistentFilesBuffer = null;
    int nonexistentFileCount = 0;
    File file;
    while (i.hasNext()) {
      file = (File)i.next();
      if (file.exists()) {
        open(file, false);
      }
      else {
        // This file has been deleted since the previous execution. Remove it
        // from the list of followed files
        attributes_.removeFollowedFile(file);
        nonexistentFileCount++;
        if (nonexistentFilesBuffer == null) {
          nonexistentFilesBuffer = new StringBuffer(file.getAbsolutePath());
        }
        else {
          nonexistentFilesBuffer.append(file.getAbsolutePath());
        }
        nonexistentFilesBuffer.append(messageLineSeparator);
      }
    }
    if (nonexistentFileCount > 0) {
      // Alert the user of the fact that one or more files have been
      // deleted since the previous execution
      String message = MessageFormat.format(
        resBundle_.getString("message.filesDeletedSinceLastExecution.text"),
        new Object [] {
          new Long(nonexistentFileCount), 
          nonexistentFilesBuffer.toString()
        }
      );
      JOptionPane.showMessageDialog(
        frame_, 
        message,
        resBundle_.getString("message.filesDeletedSinceLastExecution.title"),
        JOptionPane.WARNING_MESSAGE
      );
    }
    if (tabbedPane_.getTabCount() > 0) {
      if (tabbedPane_.getTabCount() > attributes_.getSelectedTabIndex()) {
        tabbedPane_.setSelectedIndex(attributes_.getSelectedTabIndex());
      }
      else { 
        tabbedPane_.setSelectedIndex(0); 
      }
    }
    else {
      close_.setEnabled(false);
      reload_.setEnabled(false);
      edit_.setEnabled(false);
      top_.setEnabled(false);
      bottom_.setEnabled(false);
      clear_.setEnabled(false);
      clearAll_.setEnabled(false);
      delete_.setEnabled(false);
      deleteAll_.setEnabled(false);
      pause_.setEnabled(false);
    }
  }

  /**
   * @param jMenuBar
   */
  private void initFrame(JMenuBar jMenuBar)
  {
    frame_.setJMenuBar(jMenuBar);
    frame_.getContentPane().add(toolBar_, BorderLayout.NORTH);
    frame_.getContentPane().add(tabbedPane_, BorderLayout.CENTER);
    frame_.setSize(
      attributes_.getWidth(),
      attributes_.getHeight()
    );
  }

  /**
   * Builds the menu bar for the application
   * 
   * @return reference the constructed menu bar
   */
  private JMenuBar buildMenuBar()
  {
    // file menu
    Menu fileMenu = new Menu(
      resBundle_.getString("menu.File.name"), 
      resBundle_.getString("menu.File.mnemonic")
    );
    fileMenu.addFollowAppAction(open_);
    fileMenu.addFollowAppAction(close_);
    fileMenu.addFollowAppAction(reload_);
    fileMenu.addSeparator();
    fileMenu.addFollowAppAction(pause_);
    fileMenu.addSeparator();
    fileMenu.addFollowAppAction(exit_);
    // edit menu
    Menu editMenu = new Menu(
      resBundle_.getString("menu.Edit.name"),
      resBundle_.getString("menu.Edit.mnemonic")
    );
    editMenu.addFollowAppAction(find_);
    editMenu.addSeparator();
    editMenu.addFollowAppAction(clearHighlights_);
    editMenu.addFollowAppAction(clearAllHighlights_);
    // tool menu
    Menu toolsMenu = new Menu(
      resBundle_.getString("menu.Tools.name"),
      resBundle_.getString("menu.Tools.mnemonic")
    );
    toolsMenu.addFollowAppAction(top_);
    toolsMenu.addFollowAppAction(bottom_);
    toolsMenu.addSeparator();
    toolsMenu.addFollowAppAction(clear_);
    toolsMenu.addFollowAppAction(clearAll_);
    toolsMenu.addFollowAppAction(delete_);
    toolsMenu.addFollowAppAction(deleteAll_);
    toolsMenu.addSeparator();
    toolsMenu.addFollowAppAction(configure_);
    toolsMenu.addFollowAppAction(edit_);
    // window menu
    Menu windowMenu = new Menu(
      resBundle_.getString("menu.Window.name"),
      resBundle_.getString("menu.Window.mnemonic")
    );
    windowMenu.addFollowAppAction(nextTab_);
    windowMenu.addFollowAppAction(prevTab_);
    // help menu
    Menu helpMenu = new Menu(
      resBundle_.getString("menu.Help.name"),
      resBundle_.getString("menu.Help.mnemonic")
    );
    helpMenu.addFollowAppAction(about_);
    if (DEBUG) {
        helpMenu.addSeparator();
        helpMenu.addFollowAppAction(debug_);
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

  /**
   * Builds the popup menu shown when right clicking in a text area.
   */
  private void buildPopupMenu()
  {
    popupMenu_ = new PopupMenu();
    popupMenu_.addFollowAppAction(open_);
    popupMenu_.addFollowAppAction(close_);
    popupMenu_.addFollowAppAction(reload_);
    popupMenu_.addSeparator();
    popupMenu_.addFollowAppAction(top_);    
    popupMenu_.addFollowAppAction(bottom_);
    popupMenu_.addSeparator();
    popupMenu_.addFollowAppAction(clear_);
    popupMenu_.addFollowAppAction(clearAll_);
    popupMenu_.addFollowAppAction(delete_);
    popupMenu_.addFollowAppAction(deleteAll_);
    popupMenu_.addSeparator();
    popupMenu_.addFollowAppAction(configure_);
    popupMenu_.addFollowAppAction(edit_);
  }

  /**
   * Builds the toolbar shown at the top of the application
   */
  private void buildToolBar()
  {
    toolBar_ = new ToolBar();
    toolBar_.addFollowAppAction(open_);
    toolBar_.addSeparator();
    toolBar_.addFollowAppAction(top_);
    toolBar_.addFollowAppAction(bottom_);
    toolBar_.addSeparator();
    toolBar_.addFollowAppAction(clear_);
    toolBar_.addFollowAppAction(clearAll_);
    toolBar_.addFollowAppAction(delete_);
    toolBar_.addFollowAppAction(deleteAll_);
    toolBar_.addSeparator();
    toolBar_.addFollowAppAction(pause_);
    toolBar_.addSeparator();
    toolBar_.addFollowAppAction(configure_);
  }

  private StartupStatus startupStatus_;
  
  void show () {
      frame_.show();
  }


  /* 
  Warning: This method should be called only from (1) the FollowApp 
  initializer (before any components are realized) or (2) from the event 
  dispatching thread. 
  */
  void open (
    File file, boolean addFileToAttributes, boolean startFollowing
  ) {
    FileFollowingPane fileFollowingPane = 
      (FileFollowingPane)fileToFollowingPaneMap_.get(file);
    if (fileFollowingPane != null) {
      // File is already open; merely select its tab
      tabbedPane_.setSelectedComponent(fileFollowingPane);
    }
    else {
      fileFollowingPane = new FileFollowingPane(
        file,
        attributes_.getBufferSize(),
        attributes_.getLatency(),
        attributes_.autoScroll()
      );
      JTextArea ffpTextArea = fileFollowingPane.getTextArea();
      enableDragAndDrop(ffpTextArea);
      ffpTextArea.setFont(attributes_.getFont());
      ffpTextArea.addMouseListener(getRightClickListener());
      fileToFollowingPaneMap_.put(file, fileFollowingPane);
      if (startFollowing) { fileFollowingPane.startFollowing(); }
      tabbedPane_.addTab(
        file.getName(), 
        null,
        fileFollowingPane, 
        file.getAbsolutePath()
      );
      tabbedPane_.setSelectedIndex(tabbedPane_.getTabCount() - 1);
      // add a listener to set the pause icon correctly
      fileFollowingPane.addComponentListener(new ComponentAdapter() {
        public void componentShown(ComponentEvent e)
        {
          pause_.setIconByState(((FileFollowingPane)e.getSource()).isFollowing());
        }
      });
      if (!close_.isEnabled()) {
        close_.setEnabled(true);
        reload_.setEnabled(true);
        edit_.setEnabled(true);
        top_.setEnabled(true);
        bottom_.setEnabled(true);
        clear_.setEnabled(true);
        clearAll_.setEnabled(true);
        delete_.setEnabled(true);
        deleteAll_.setEnabled(true);
        pause_.setEnabled(true);
      }
      if (addFileToAttributes) { attributes_.addFollowedFile(file); }
    }
  }

  void open (File file, boolean addFileToAttributes) {
    open(file, addFileToAttributes, attributes_.autoScroll());
  }

  /**
  Warning: This method should be called only from the event 
  dispatching thread. 
  @param cursorType may be Cursor.DEFAULT_CURSOR or Cursor.WAIT_CURSOR
  */
  void setCursor (int cursorType) {
    if (cursorType == currentCursor_) { return; }    
    switch (cursorType) {
      case Cursor.DEFAULT_CURSOR :
        if (defaultCursor_ == null) {
          defaultCursor_ = Cursor.getDefaultCursor();
        }
        frame_.setCursor(defaultCursor_);
        break;

      case Cursor.WAIT_CURSOR :
        if (waitCursor_ == null) {
          waitCursor_ = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        }
        frame_.setCursor(waitCursor_);
        break;

      default:
        throw new IllegalArgumentException(
          "Supported cursors are Cursor.DEFAULT_CURSOR and Cursor.WAIT_CURSOR"
        );
    }
    currentCursor_ = cursorType;
  }
  int currentCursor_ = Cursor.DEFAULT_CURSOR;
  Cursor defaultCursor_;
  Cursor waitCursor_;
  
  // Lazy initializer for the right-click listener which invokes a popup menu
  private MouseListener getRightClickListener () {
    if (rightClickListener_ == null) {
      rightClickListener_ = new MouseAdapter() {
        public void mouseReleased (MouseEvent e) {
  if (SwingUtilities.isRightMouseButton(e)) {
    Component source = e.getComponent();
    popupMenu_.show(source, e.getX(), e.getY());
  }
        }
      };
    }
    return rightClickListener_;
  }
  
  void enableDragAndDrop (Component c) {
    // Invoking this constructor automatically sets the component's drop target
    new DropTarget(c, new DndFileOpener(this));
  }
  void disableDragAndDrop (Component c) { c.setDropTarget(null); }

  FileFollowingPane getSelectedFileFollowingPane () {
    return (FileFollowingPane)tabbedPane_.getSelectedComponent();
  }
  List getAllFileFollowingPanes () {
    int tabCount = tabbedPane_.getTabCount();
    List allFileFollowingPanes = new ArrayList();
    for (int i=0; i < tabCount; i++) {
      allFileFollowingPanes.add(tabbedPane_.getComponentAt(i));
    }
    return allFileFollowingPanes;
  }

  FollowAppAttributes attributes_;
  Map fileToFollowingPaneMap_ = new HashMap();
  JFrame frame_;
  JTabbedPane tabbedPane_;
  ToolBar toolBar_;
  PopupMenu popupMenu_;
  private MouseListener rightClickListener_;
  ResourceBundle resBundle_ =
    ResourceBundle.getBundle("ghm.followgui.FollowAppResourceBundle");

  // Actions
  Open open_;
  Close close_;
  Reload reload_;
  Edit edit_;
  Exit exit_;
  Top top_;
  Bottom bottom_;
  Clear clear_;
  ClearAll clearAll_;
  Delete delete_;
  DeleteAll deleteAll_;
  Configure configure_;
  About about_;
  Debug debug_;
  Pause pause_;
  NextTab nextTab_;
  PreviousTab prevTab_;
  Find find_;
  ClearHighlights clearHighlights_;
  ClearAllHighlights clearAllHighlights_;

  SystemInterface systemInterface_;

  static final String fileSeparator = System.getProperty("file.separator");
  static final String messageLineSeparator = "\n";
    static final boolean DEBUG = Boolean.getBoolean("follow.debug");
    static boolean HAS_SOLARIS_BUG = false;

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

  static void centerWindowInScreen (Window window) {
    Dimension screenSize = window.getToolkit().getScreenSize();
    Dimension windowSize = window.getPreferredSize();
    window.setLocation(
      (int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
      (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2)
    );
  }

  /**
  Invoke this method to start the Follow application.  If any command-line
  arguments are passed in, they are assume to be filenames and are opened
  in the Follow application
  @param args files to be opened
  */  
  public static void main (String[] args) 
  throws IOException, InterruptedException, InvocationTargetException {
    instance_ = new FollowApp(args);
    SwingUtilities.invokeAndWait(new Runnable() { public void run () {
      // ensure all widgets inited before opening files
      instance_.show();
      instance_.startupStatus_.markDone(instance_.startupStatus_.CREATE_WIDGETS);
    }});
    instance_.startupStatus_.dispose();
    // commented code below so that windows follow based on setting in
    // preferences which is set on the pane when the file is opened
//    for (int i=0; i < instance_.tabbedPane_.getTabCount(); i++) {
//      ((FileFollowingPane)instance_.tabbedPane_.getComponentAt(i)).startFollowing();
//    }
  }
  static FollowApp instance_;
}
