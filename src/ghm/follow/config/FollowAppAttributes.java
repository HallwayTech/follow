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

package ghm.follow.config;

import ghm.follow.gui.FollowApp;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * @author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
public class FollowAppAttributes {

	public FollowAppAttributes(FollowApp app) throws IOException {
		if (!(PROPERTY_FILE.exists())) {
			// If the property file doesn't exist, we create a default property
			// file using a prototype property file stored somewhere on the
			// classpath
			System.out.println("No property file for the Follow application is present; creating "
					+ PROPERTY_FILE.getAbsolutePath() + " (with default values) ...");
			properties_ = (EnumeratedProperties) getDefaultProperties().clone();
			System.out.println("... property file created successfully.");
		}
		else {
			properties_ = new EnumeratedProperties();
			FileInputStream fis = new FileInputStream(PROPERTY_FILE);
			properties_.load(fis);
			switch (getAttributesVersion()) {
				case UNVERSIONED:
					// Migrate unversioned attributes to 1.1 attributes
					System.out.println("Migrating pre-v1.1 properties to v1.1.");
					setAttributesVersion(v1_1);
					setTabPlacement(getDefaultAttributes().getTabPlacement());
				case v1_1:
					// Migrate 1.1 attributes to 1.2 attributes
					System.out.println("Migrating v1.1 properties to v1.2.");
					setAttributesVersion(v1_2);
					setFont(getDefaultAttributes().getFont());
				case v1_2:
					// Migrate 1.2 attributes to 1.3 attributes
					System.out.println("Migrating v1.2 properties to v1.3.");
					setAttributesVersion(v1_3);
					setConfirmDelete(true);
					setConfirmDeleteAll(true);
					// Additionally, it is necessary to warn the user about the
					// changes to
					// Clear and ClearAll and the introduction of Delete and
					// DeleteAll
					JOptionPane.showMessageDialog(null, app.getResourceBundle().getString(
							"v1.3.warning.text"), app.getResourceBundle().getString(
							"v1.3.warning.title"), JOptionPane.WARNING_MESSAGE);
				case v1_3:
				case v1_3_2:
					// Migrate 1.3 attributes to 1.4 attributes
					System.out.println("Migrating v1.3 properties to v1.4.");
					setAttributesVersion(v1_4);
					setAutoScroll(true);
					// Inform the user of the new AutoScroll feature
					JOptionPane.showMessageDialog(null, app.getResourceBundle().getString(
							"v1.4.info.text"),
							app.getResourceBundle().getString("v1.4.info.title"),
							JOptionPane.INFORMATION_MESSAGE);
				case v1_4:
					// Migrate 1.4 attributes to 1.5 attributes
					System.out.println("Migrating v1.4 properties to v.1.5.");
					setAttributesVersion(v1_5_0);
					setTabSize(4);
				case v1_5_0:
					// Migrate 1.5.0 attributes to 1.6.0 attributes
					System.out.println("Migrating v1.5 properties to 1.6.0.");
					setAttributesVersion(v1_6_0);
					setRecentFilesMax(5);
			}
			fis.close();
		}
	}

	private FollowAppAttributes(EnumeratedProperties props) throws IOException {
		properties_ = props;
	}

	public int getHeight() {
		return getInt(HEIGHT_KEY);
	}

	public void setHeight(int height) {
		setInt(HEIGHT_KEY, height);
	}

	public int getWidth() {
		return getInt(WIDTH_KEY);
	}

	public void setWidth(int width) {
		setInt(WIDTH_KEY, width);
	}

	public int getX() {
		return getInt(X_KEY);
	}

	public void setX(int x) {
		setInt(X_KEY, x);
	}

	public int getY() {
		return getInt(Y_KEY);
	}

	public void setY(int y) {
		setInt(Y_KEY, y);
	}

	/**
	 * Get an array files being followed
	 * 
	 * @return File[] File array of followed files
	 */
	public File[] getFollowedFiles() {
		return getFiles(getFollowedFilesList());
	}

	/**
	 * Get a list of files being followed
	 * 
	 * @return List file names as Strings
	 */
	private List getFollowedFilesList() {
		return properties_.getEnumeratedProperty(FOLLOWED_FILES_KEY);
	}

	protected File[] getFiles(List fileList) {
		File[] files = new File[fileList.size()];
		Iterator i = fileList.iterator();
		int count = 0;
		while (i.hasNext()) {
			files[count++] = new File((String) i.next());
		}
		return files;
	}

	/**
	 * Checks the existence of a file in the list of followed files
	 * 
	 * @return true iff any File in the List of followed Files
	 *         (getFollowedFiles()) has the same Canonical Path as the supplied
	 *         File
	 */
	public boolean followedFileListContains(File file) {
		return fileListContains(getFollowedFilesList(), file);
	}

	/**
	 * Checks the existence of a file in the list of recent files
	 * 
	 * @return true iff any File in the List of recent Files
	 *         (getFollowedFiles()) has the same Canonical Path as the supplied
	 *         File
	 */
	public boolean recentFileListContains(File file) {
		return fileListContains(getRecentFilesList(), file);
	}

	/**
	 * @return true iff any File in the List of Files (getFollowedFiles()) has
	 *         the same Canonical Path as the supplied File
	 */
	protected boolean fileListContains(List fileList, File file) {
		boolean retval = false;
		if (fileList != null && file != null) {
			for (int i = 0; i < fileList.size(); i++) {
				String nextFile = (String) fileList.get(i);
				try {
					if (nextFile.equals(file.getCanonicalPath())) {
						retval = true;
						break;
					}
				}
				catch (IOException e) {
					break;
				}
			}
		}
		return retval;
	}

	/**
	 * Adds a file to the list of followed files
	 * 
	 * @param file
	 */
	public void addFollowedFile(File file) {
		List fileNames = properties_.getEnumeratedProperty(FOLLOWED_FILES_KEY);
		fileNames.add(file.getAbsolutePath());
		properties_.setEnumeratedProperty(FOLLOWED_FILES_KEY, fileNames);
	}

	/**
	 * Removes a file from the list of followed files
	 * 
	 * @param file
	 */
	public void removeFollowedFile(File file) {
		List fileNames = properties_.getEnumeratedProperty(FOLLOWED_FILES_KEY);
		fileNames.remove(file.getAbsolutePath());
		properties_.setEnumeratedProperty(FOLLOWED_FILES_KEY, fileNames);
	}

	public int getTabPlacement() {
		return getInt(TAB_PLACEMENT_KEY);
	}

	public void setTabPlacement(int tabPlacement) {
		setInt(TAB_PLACEMENT_KEY, tabPlacement);
	}

	public int getTabSize() {
		return getInt(TAB_SIZE_KEY);
	}

	public void setTabSize(int tabSize) {
		setInt(TAB_SIZE_KEY, tabSize);
	}

	public void setTabSize(String tabSize) {
		setTabSize(Integer.parseInt(tabSize));
	}

	public int getSelectedTabIndex() {
		try {
			return getInt(SELECTED_TAB_INDEX_KEY);
		}
		catch (NumberFormatException e) {
			setSelectedTabIndex(0);
			return 0;
		}
	}

	public void setSelectedTabIndex(int selectedTabIndex) {
		setInt(SELECTED_TAB_INDEX_KEY, selectedTabIndex);
	}

	public File getLastFileChooserDirectory() {
		return new File(properties_.getProperty(LAST_FILE_CHOOSER_DIR_KEY, userHome));
	}

	public void setLastFileChooserDirectory(File file) {
		properties_.setProperty(LAST_FILE_CHOOSER_DIR_KEY, file.getAbsolutePath());
	}

	public int getBufferSize() {
		return getInt(BUFFER_SIZE_KEY);
	}

	public void setBufferSize(int bufferSize) {
		setInt(BUFFER_SIZE_KEY, bufferSize);
	}

	public void setBufferSize(String bufferSize) {
		setBufferSize(Integer.parseInt(bufferSize));
	}

	public int getLatency() {
		return getInt(LATENCY_KEY);
	}

	public void setLatency(int latency) {
		setInt(LATENCY_KEY, latency);
	}

	public void setLatency(String latency) {
		setLatency(Integer.parseInt(latency));
	}

	public int getAttributesVersion() {
		if (properties_.get(ATTRIBUTES_VERSION_KEY) == null) {
			// Supporting v1.0 & v1.0.1, which had no notion of attributes
			// version
			return UNVERSIONED;
		}
		else {
			return getInt(ATTRIBUTES_VERSION_KEY);
		}
	}

	public void setAttributesVersion(int attributesVersion) {
		setInt(ATTRIBUTES_VERSION_KEY, attributesVersion);
	}

	public Font getFont() {
		Font font = new Font(properties_.getProperty(FONT_FAMILY_KEY), getInt(FONT_STYLE_KEY),
				getInt(FONT_SIZE_KEY));
		return font;
	}

	public void setFont(Font font) {
		properties_.setProperty(FONT_FAMILY_KEY, font.getFontName());
		setInt(FONT_STYLE_KEY, font.getStyle());
		setInt(FONT_SIZE_KEY, font.getSize());
	}

	public boolean confirmDelete() {
		return getBoolean(CONFIRM_DELETE_KEY);
	}

	public void setConfirmDelete(boolean value) {
		setBoolean(CONFIRM_DELETE_KEY, value);
	}

	public boolean confirmDeleteAll() {
		return getBoolean(CONFIRM_DELETE_ALL_KEY);
	}

	public void setConfirmDeleteAll(boolean value) {
		setBoolean(CONFIRM_DELETE_ALL_KEY, value);
	}

	public boolean autoScroll() {
		return getBoolean(AUTO_SCROLL_KEY);
	}

	public void setAutoScroll(boolean value) {
		setBoolean(AUTO_SCROLL_KEY, value);
	}

	public String getEditor() {
		String result = properties_.getProperty(EDITOR_KEY);
		if (result == null) {
			result = "";
		}

		return (result);
	}

	public void setEditor(String value) {
		properties_.setProperty(EDITOR_KEY, value);
	}

	/**
	 * Adds a file to the list of recent files
	 * 
	 * @param file
	 */
	public void addRecentFile(File file) {
		if (!recentFileListContains(file)) {
			List fileList = getRecentFilesList();
			// check size constraint and add accordingly
			if (fileList.size() == getRecentFilesMax()) {
				for (int i = 0; i < fileList.size() - 1; i++) {
					fileList.set(i, fileList.get(i + 1));
				}
				fileList.set(fileList.size() - 1, file.getAbsolutePath());
			}
			else {
				fileList.add(file.getAbsolutePath());
			}
			properties_.setEnumeratedProperty(RECENT_FILES_KEY, fileList);
		}
	}

	/**
	 * Get a list of recently opened files
	 * 
	 * @return List recently opened files as Strings
	 */
	private List getRecentFilesList() {
		return properties_.getEnumeratedProperty(RECENT_FILES_KEY);
	}

	/**
	 * Get an array of recently opened files
	 * 
	 * @return File[] File array of followed files
	 */
	public File[] getRecentFiles() {
		return getFiles(getRecentFilesList());
	}

	public int getRecentFilesMax() {
		return getInt(RECENT_FILES_MAX_KEY);
	}

	public void setRecentFilesMax(String max) {
		setRecentFilesMax(Integer.parseInt(max));
	}

	public void setRecentFilesMax(int max) {
		List files = getRecentFilesList();
		if (files.size() > max) {
			for (int i = files.size() - max; i > 0; i--) {
				files.remove(0);
			}
			properties_.setEnumeratedProperty(RECENT_FILES_KEY, files);
		}
		setInt(RECENT_FILES_MAX_KEY, max);
	}

	public void store() throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(propertyFileName));
		properties_.store(bos, null);
		bos.close();
	}

	private int getInt(String key) {
		return Integer.parseInt(properties_.getProperty(key));
	}

	private void setInt(String key, int value) {
		properties_.setProperty(key, String.valueOf(value));
	}

	private boolean getBoolean(String key) {
		return "true".equals(properties_.getProperty(key));
	}

	private void setBoolean(String key, boolean value) {
		properties_.setProperty(key, String.valueOf(value));
	}

	public FollowAppAttributes getDefaultAttributes() throws IOException {
		if (defaultAttributes_ == null) {
			defaultAttributes_ = new FollowAppAttributes(getDefaultProperties());
			// Check for the unlikely possibility that the default font is
			// unavailable
			Font defaultFont = defaultAttributes_.getFont();
			String[] availableFontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getAvailableFontFamilyNames();
			boolean defaultFontIsAvailable = false;
			for (int i = 0; i < availableFontFamilyNames.length; i++) {
				if (defaultFont.getFamily().equals(availableFontFamilyNames[i])) {
					defaultFontIsAvailable = true;
					break;
				}
			}
			if (!defaultFontIsAvailable) {
				System.out.println("Font family " + defaultFont.getFamily()
						+ " is unavailable; using " + availableFontFamilyNames[0] + " instead.");
				defaultAttributes_.setFont(new Font(availableFontFamilyNames[0], defaultFont
						.getStyle(), defaultFont.getSize()));
			}
		}
		return defaultAttributes_;
	}

	private EnumeratedProperties getDefaultProperties() throws IOException {
		if (defaultProperties_ == null) {
			InputStream in = this.getClass().getResourceAsStream(PROPERTY_PROTOTYPE_FILE_NAME);
			BufferedInputStream bis = new BufferedInputStream(in);
			FileOutputStream fos = new FileOutputStream(PROPERTY_FILE);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			byte[] byteArray = new byte[BUFFER_SIZE];
			int len;
			while ((len = bis.read(byteArray, 0, BUFFER_SIZE)) > 0) {
				bos.write(byteArray, 0, len);
			}
			bos.flush();
			bos.close();
			bis.close();
			defaultProperties_ = new EnumeratedProperties();
			defaultProperties_.load(new BufferedInputStream(new FileInputStream(PROPERTY_FILE)));
		}
		return defaultProperties_;
	}

	EnumeratedProperties properties_;
	private EnumeratedProperties defaultProperties_;
	private FollowAppAttributes defaultAttributes_;
	static final String userHome = System.getProperty("user.home");
	static final String propertyFileName = userHome + FollowApp.FILE_SEPARATOR
			+ ".followApp.properties";
	public static final File PROPERTY_FILE = new File(propertyFileName);
	public static final String PROPERTY_PROTOTYPE_FILE_NAME = "followApp.properties.prototype";
	public static final int BUFFER_SIZE = 32768;

	public static final String HEIGHT_KEY = "height";
	public static final String WIDTH_KEY = "width";
	public static final String X_KEY = "x";
	public static final String Y_KEY = "y";
	public static final String FOLLOWED_FILES_KEY = "followedFiles";
	public static final String TAB_PLACEMENT_KEY = "tabs.placement";
	public static final String SELECTED_TAB_INDEX_KEY = "tabs.selectedIndex";
	public static final String LAST_FILE_CHOOSER_DIR_KEY = "fileChooser.lastDir";
	public static final String BUFFER_SIZE_KEY = "bufferSize";
	public static final String LATENCY_KEY = "latency";
	public static final String ATTRIBUTES_VERSION_KEY = "attributesVersion";
	public static final String FONT_FAMILY_KEY = "fontFamily";
	public static final String FONT_STYLE_KEY = "fontStyle";
	public static final String FONT_SIZE_KEY = "fontSize";
	public static final String CONFIRM_DELETE_KEY = "confirmDelete";
	public static final String CONFIRM_DELETE_ALL_KEY = "confirmDeleteAll";
	public static final String AUTO_SCROLL_KEY = "autoScroll";
	public static final String EDITOR_KEY = "editor";
	public static final String TAB_SIZE_KEY = "tabSize";
	public static final String RECENT_FILES_MAX_KEY = "recentFilesMax";
	public static final String RECENT_FILES_KEY = "recentFiles";

	// Versions
	public static final int UNVERSIONED = 0;
	public static final int v1_1 = 1;
	public static final int v1_2 = 2;
	public static final int v1_3 = 3;
	public static final int v1_3_2 = 4;
	public static final int v1_4 = 5;
	public static final int v1_5_0 = 6;
	public static final int v1_6_0 = 7;
}