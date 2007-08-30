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

import ghm.follow.InvalidVkException;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import org.apache.log4j.Logger;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/**
 * Base class for all actions in the Follow application.
 * 
 * @author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
public abstract class FollowAppAction extends AbstractAction {

	public FollowAppAction(FollowApp app, String name, String mnemonic, String accelerator) {
		super(name);
		init(app, mnemonic, accelerator);
	}

	public FollowAppAction(FollowApp app, String name, String mnemonic, String accelerator,
			String iconName) {
		super(name, new ImageIcon(app.getClass().getResource(iconName)));
		init(app, mnemonic, accelerator);
	}

	private void init(FollowApp app, String mnemonic, String accelerator) {
		app_ = app;
		try {
			setMnemonic(mnemonic);
		}
		catch (InvalidVkException e) {
			getLog().error("Invalid mnemonic", e);
		}
		try {
			setAccelerator(accelerator);
		}
		catch (InvalidVkException e) {
			getLog().error("Invalid accelerator", e);
		}
	}

	int getMnemonic() {
		return mnemonic_;
	}

	void setMnemonic(int mnemonic) {
		mnemonic_ = mnemonic;
	}

	void setMnemonic(String mnemonic) throws InvalidVkException {
		if (mnemonic != null && mnemonic.length() > 0) {
			setMnemonic(mnemonic.charAt(0));
		}
	}

	KeyStroke getAccelerator() {
		return accelerator_;
	}

	void setAccelerator(KeyStroke accelerator) {
		accelerator_ = accelerator;
	}

	void setAccelerator(String accelerator) throws InvalidVkException {
		if (accelerator != null && accelerator.length() > 0) {
			setAccelerator(KeyStroke.getKeyStroke(findKeyEventVk(accelerator), KeyEvent.CTRL_MASK));
		}
	}

	private int findKeyEventVk(String key) throws InvalidVkException {
		if (!key.startsWith("VK_")) {
			key = "VK_" + key;
		}
		try {
			Field field = KeyEvent.class.getDeclaredField(key.toUpperCase());
			return field.getInt(KeyEvent.class);
		}
		catch (NoSuchFieldException e) {
			throw new InvalidVkException("Unable to match mnemonic to a field in KeyEvent", e);
		}
		catch (IllegalAccessException e) {
			throw new InvalidVkException(e.getMessage(), e);
		}
	}

	public FollowApp getApp() {
		return app_;
	}

	private transient Logger log;

	private Logger getLog() {
		if (log == null) {
			log = Logger.getLogger(FollowAppAction.class.getName());
		}
		return log;
	}

	private FollowApp app_;
	private int mnemonic_;
	private KeyStroke accelerator_;
}