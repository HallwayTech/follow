// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
Implementation of <code>java.awt.dnd.DropTargetListener</code> which
opens files dropped on the Follow application's tabbed pane.

@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class DndFileOpener implements DropTargetListener {

  DndFileOpener (FollowApp app) {
    app_ = app;
  }
  
  /**
  If the DropTargetDropEvent's DataFlavor is javaFileListFlavor, it opens
  the List of dropped files in the Follow application. No other DataFlavors 
  are supported.
  @param e "drop" event
  @see java.awt.dnd.DropTargetListener#drop(DropTargetDropEvent)
  */
  public void drop (DropTargetDropEvent e) {
    DataFlavor[] flavors = e.getCurrentDataFlavors();
    int numFlavors = (flavors != null) ? flavors.length : 0;
    for (int i=0; i < numFlavors; i++) {
      // Ignore all flavors except javaFileListType
      if (flavors[i].isFlavorJavaFileListType()) {
        e.acceptDrop(DnDConstants.ACTION_COPY);
        boolean dropCompleted = false;
        Transferable transferable = e.getTransferable();
        try {
          List fileList = (List)transferable.getTransferData(flavors[i]);
          Iterator iterator = fileList.iterator();
          while (iterator.hasNext()) {
            app_.open((File)iterator.next(), true);
          }
          dropCompleted = true;
        }
        catch (UnsupportedFlavorException ufException) { /* do nothing */ }
        catch (IOException ioException) { /* do nothing */ }
        finally { e.dropComplete(dropCompleted); }
      }
    }
  }

  /** Does nothing. */
  public void dragEnter (DropTargetDragEvent e) {}

  /** Does nothing. */
  public void dragOver (DropTargetDragEvent e) {}

  /** Does nothing. */
  public void dragExit (DropTargetEvent e) {}

  /** Does nothing. */
  public void dragScroll (DropTargetDragEvent e) {}

  /** Does nothing. */
  public void dropActionChanged (DropTargetDragEvent e) {}

  FollowApp app_;
  
}

