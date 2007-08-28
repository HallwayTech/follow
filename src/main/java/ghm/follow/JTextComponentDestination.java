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

package ghm.follow;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 * Implementation of {@link OutputDestination} which appends Strings to a
 * {@link JTextComponent}.
 * 
 * @see OutputDestination
 * @see JTextCompnent
 * @author <a href="mailto:carl.hall@gmail.com">Carl Hall</a>
 */
public class JTextComponentDestination extends FilterableOutputDestination {
	protected JTextComponent _comp;
	protected boolean autoPositionCaret_;

	/**
	 * Construct a new JTextCompnentDestination.
	 * 
	 * @param jTextPane
	 *            text will be appended to this text area
	 * @param autoPositionCaret
	 *            if true, caret will be automatically moved to the bottom of
	 *            the text area when text is appended
	 */
	public JTextComponentDestination(JTextComponent jTextComponent, boolean autoPositionCaret) {
		_comp = jTextComponent;
		autoPositionCaret_ = autoPositionCaret;
	}

	public JTextComponent getJTextComponent() {
		return _comp;
	}

	public void setJTextComponent(JTextComponent comp) {
		_comp = comp;
	}

	/**
	 * Add a filtered view to this destination. Filtered views show only a
	 * subset of the total output based on filter conditions.
	 * 
	 * @since 1.8.0
	 */
	public void addFilteredView() {

	}

	/**
	 * Remove a filtered view
	 * 
	 * @since 1.8.0
	 */
	public void removeFilteredView() {

	}

	/**
	 * @return whether caret will be automatically moved to the bottom of the
	 *         text area when text is appended
	 */
	public boolean autoPositionCaret() {
		return autoPositionCaret_;
	}

	/**
	 * @param autoPositionCaret
	 *            if true, caret will be automatically moved to the bottom of
	 *            the text area when text is appended
	 */
	public void setAutoPositionCaret(boolean autoPositionCaret) {
		autoPositionCaret_ = autoPositionCaret;
	}

	public void handlePrint(String s) {
		try {
			_comp.getDocument().insertString(_comp.getDocument().getLength(), s, null);
			if (autoPositionCaret_) {
				_comp.setCaretPosition(_comp.getDocument().getLength());
			}
		}
		catch (BadLocationException e) {
			// just ignore, nothing we can do
			getLog().log(Level.SEVERE, "BadLocationException in JTextComponentDestination", e);
		}
	}

	public void clear() {
		_comp.setText("");
		if (autoPositionCaret_) {
			_comp.setCaretPosition(0);
		}
	}

	private transient Logger log;

	private Logger getLog() {
		if (log == null) {
			log = Logger.getLogger(JTextComponentDestination.class.getName());
		}
		return log;
	}
}