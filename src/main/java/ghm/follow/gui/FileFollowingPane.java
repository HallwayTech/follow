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

package ghm.follow.gui;

import ghm.follow.FileFollower;
import ghm.follow.JTextComponentDestination;
import ghm.follow.OutputDestination;
import ghm.follow.search.SearchableTextPane;

import java.awt.Font;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.log4j.Logger;

/**
 * A component which allows one to view a text file to which information is
 * being asynchronously appended.
 * 
 * @author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
public class FileFollowingPane extends JScrollPane {
	/** FileFollower used to print to this component */
	protected FileFollower _fileFollower;

	/** Text area into which followed file's contents are printed */
	protected SearchableTextPane _textArea;

	/** OutputDestination used w/FileFollower */
	protected JTextComponentDestination _destination;

	/**
	 * @param file
	 *            text file to be followed
	 * @param bufferSize
	 *            size of the character buffer inside the FileFollower used to
	 *            follow the supplied file
	 * @param latency
	 *            latency of the FileFollower used to follow the supplied file
	 */
	public FileFollowingPane(File file, int bufferSize, int latency, boolean autoPositionCaret,
			Font font, int tabSize) {
		_textArea = new SearchableTextPane(font, tabSize);
		_textArea.setEditable(false);
		_textArea.setUI(new LineTextUI());
		_destination = new JTextComponentDestination(_textArea, autoPositionCaret);
		_fileFollower = new FileFollower(file, bufferSize, latency,
				new OutputDestination[] { _destination });
		add(_textArea);
		setViewportView(_textArea);
	}

	/**
	 * Returns the text area to which the followed file's contents are being
	 * printed.
	 * 
	 * @return text area containing followed file's contents
	 */
	public SearchableTextPane getTextPane() {
		return _textArea;
	}

	/**
	 * Returns whether caret is automatically repositioned to the end of the
	 * text area when text is appended to the followed file
	 * 
	 * @return whether caret is automatically repositioned on append
	 */
	public boolean autoPositionCaret() {
		return _destination.autoPositionCaret();
	}

	/**
	 * Sets whether caret is automatically repositioned to the end of the text
	 * area when text is appended to the followed file
	 * 
	 * @param value
	 *            whether caret is automatically repositioned on append
	 */
	public void setAutoPositionCaret(boolean value) {
		_destination.setAutoPositionCaret(value);
	}

	/**
	 * Returns the FileFollower which is being used to print information in this
	 * component.
	 * 
	 * @return FileFollower used by this component
	 */
	public FileFollower getFileFollower() {
		return _fileFollower;
	}

	/**
	 * Convenience method; equivalent to calling
	 * getFileFollower().getFollowedFile()
	 */
	public File getFollowedFile() {
		return _fileFollower.getFollowedFile();
	}

	/**
	 * Convenience method; equivalent to calling getFileFollower().start()
	 */
	public void startFollowing() {
		_fileFollower.start();
	}

	/**
	 * Convenience method; equivalent to calling getFileFollower().stop()
	 */
	public void stopFollowing() {
		_fileFollower.stop();
	}

	/**
	 * Convenience method; equivalent to calling getFileFollower().restart()
	 */
	public void restartFollowing() {
		_fileFollower.restart();
	}

	/**
	 * Convenience method; equivalent to calling getFileFollower().pause()
	 */
	public void pauseFollowing() {
		_fileFollower.pause();
	}

	/**
	 * Convenience method; equivalent to calling getFileFollower().unpause()
	 */
	public void unpauseFollowing() {
		_fileFollower.unpause();
	}

	/**
	 * Convenience method; equivalent to calling getFileFollower().isPaused()
	 * 
	 * @return
	 */
	public boolean isFollowingPaused() {
		return _fileFollower.isPaused();
	}

	/**
	 * Convenience method; equivalent to calling getFileFollower().stopAndWait()
	 */
	public void stopFollowingAndWait() throws InterruptedException {
		_fileFollower.stopAndWait();
	}

	/**
	 * Convenience method; equivalent to called
	 * getFileFollower().isBeingFollowed()
	 * 
	 * @return
	 */
	public boolean isFollowing() {
		return _fileFollower.isBeingFollowed();
	}

	/**
	 * Clears the contents of this FileFollowingPane synchronously.
	 */
	public void clear() throws IOException {
		if (_fileFollower.getFollowedFile().length() == 0L) {
			return;
		}
		synchronized (_fileFollower) {
			try {
				_fileFollower.stopAndWait();
			}
			catch (InterruptedException interruptedException) {
				// Handle this better later
				getLog().error("InterrupedException in FileFollowingPane", interruptedException);
			}

			// This has the effect of clearing the contents of the followed file
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(_fileFollower
					.getFollowedFile()));
			bos.close();

			// Update textarea contents to reflect freshly cleared file
			Document doc = _textArea.getDocument();
			try {
				doc.remove(0, doc.getLength());
			}
			catch (BadLocationException e) {
				// Handle this better later
				getLog().error("BadLocationException in FileFolloingPane", e);
			}

			_fileFollower.start();
		}
	}

	private transient Logger log;

	private Logger getLog() {
		if (log == null) {
			log = Logger.getLogger(FileFollowingPane.class);
		}
		return log;
	}
}