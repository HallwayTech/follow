/* 
Copyright (C) 2000, 2001 Greg Merrill (greghmerrill@yahoo.com)

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

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

/**
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class FollowAppAttributes {
  
  FollowAppAttributes (FollowApp app) throws IOException {
    if (!(propertyFile.exists())) {
      // If the property file doesn't exist, we create a default property 
      // file using a prototype property file stored somewhere on the classpath
      System.out.println(
        "No property file for the Follow application is present; creating " +
        propertyFile.getAbsolutePath() + " (with default values) ..."
      );
      properties_ = (EnumeratedProperties)getDefaultProperties().clone();
      System.out.println("... property file created successfully.");
    }
    else {
      properties_ = new EnumeratedProperties();
      properties_.load(new FileInputStream(propertyFile));
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
          // Additionally, it is necessary to warn the user about the changes to 
          // Clear and ClearAll and the introduction of Delete and DeleteAll
          JOptionPane.showMessageDialog(
            null, 
            app.resBundle_.getString("v1.3.warning.text"),
            app.resBundle_.getString("v1.3.warning.title"),
            JOptionPane.WARNING_MESSAGE
          );
        case v1_3:
        case v1_3_2:
          // Migrate 1.3 attributes to 1.4 attributes
          System.out.println("Migrating v1.3 properties to v1.4.");
          setAttributesVersion(v1_4);
          setAutoScroll(true);
          // Inform the user of the new AutoScroll feature
          JOptionPane.showMessageDialog(
            null, 
            app.resBundle_.getString("v1.4.info.text"),
            app.resBundle_.getString("v1.4.info.title"),
            JOptionPane.INFORMATION_MESSAGE
          );
      }
    }
  }

  private FollowAppAttributes (EnumeratedProperties props) throws IOException {
    properties_ = props;
  }
  
  int getHeight () { return getInt(heightKey); }
  void setHeight (int height) { setInt(heightKey, height); }
  
  int getWidth () { return getInt(widthKey); }
  void setWidth (int width) { setInt(widthKey, width); }

  int getX () { return getInt(xKey); }
  void setX (int x) { setInt(xKey, x); }
  
  int getY () { return getInt(yKey); }
  void setY (int y) { setInt(yKey, y); }

  List getFollowedFiles () {
    List fileNames = properties_.getEnumeratedProperty(followedFilesKey);
    List files = new ArrayList();
    Iterator i = fileNames.iterator();
    while (i.hasNext()) {
      files.add(new File((String)i.next()));
    }
    return files;
  }

  void addFollowedFile (File file) {
    List fileNames = properties_.getEnumeratedProperty(followedFilesKey);
    fileNames.add(file.getAbsolutePath());
    properties_.setEnumeratedProperty(
      followedFilesKey,
      fileNames
    );
  }
  
  void removeFollowedFile (File file) {
    List fileNames = properties_.getEnumeratedProperty(followedFilesKey);
    fileNames.remove(file.getAbsolutePath());
    properties_.setEnumeratedProperty(
      followedFilesKey,
      fileNames
    );
  }

  int getTabPlacement () { return getInt(tabPlacementKey); }
  void setTabPlacement (int tabPlacement) {
    setInt(tabPlacementKey, tabPlacement);
  }
  
  int getSelectedTabIndex () { 
    try { return getInt(selectedTabIndexKey); }
    catch (NumberFormatException e) {
      throw new NoTabSelectedException();
    }
  }
  void setSelectedTabIndex (int selectedTabIndex) {
    setInt(selectedTabIndexKey, selectedTabIndex);
  }
  
  File getLastFileChooserDirectory () {
    return new File(properties_.getProperty(lastFileChooserDirKey, userHome));
  }
  void setLastFileChooserDirectory (File file) {
    properties_.setProperty(lastFileChooserDirKey, file.getAbsolutePath());
  }
  
  int getBufferSize () { return getInt(bufferSizeKey); }
  void setBufferSize (int bufferSize) { 
    setInt(bufferSizeKey, bufferSize); 
  }  
  void setBufferSize (String bufferSize) { 
    setBufferSize(Integer.parseInt(bufferSize));
  }
  
  int getLatency () { return getInt(latencyKey); }
  void setLatency (int latency) { setInt(latencyKey, latency); }
  void setLatency (String latency) { 
    setLatency(Integer.parseInt(latency));
  }
  
  int getAttributesVersion () {     
    if (properties_.get(attributesVersionKey) == null) {
      // Supporting v1.0 & v1.0.1, which had no notion of attributes version
      return UNVERSIONED;
    }
    else {
      return getInt(attributesVersionKey);
    }
  }
  void setAttributesVersion (int attributesVersion) { 
    setInt(attributesVersionKey, attributesVersion); 
  }

  Font getFont () {
    Font font = new Font(
      properties_.getProperty(fontFamilyKey),
      getInt(fontStyleKey),
      getInt(fontSizeKey)
    );
    return font;
  }
  void setFont (Font font) {
    properties_.setProperty(fontFamilyKey, font.getFontName());
    setInt(fontStyleKey, font.getStyle());
    setInt(fontSizeKey, font.getSize());
  }
  
  public boolean confirmDelete () { return getBoolean(confirmDeleteKey); }
  public void setConfirmDelete (boolean value) {
    setBoolean(confirmDeleteKey, value);
  }
  
  public boolean confirmDeleteAll () { return getBoolean(confirmDeleteAllKey); }
  public void setConfirmDeleteAll (boolean value) {
    setBoolean(confirmDeleteAllKey, value);
  }

  public boolean autoScroll () { return getBoolean(autoScrollKey); }
  public void setAutoScroll (boolean value) { setBoolean(autoScrollKey, value); }

  void store () throws IOException {
    properties_.store(
      new BufferedOutputStream(new FileOutputStream(propertyFileName)), 
      null
    );
  }
  
  private int getInt (String key) {
    return Integer.parseInt(properties_.getProperty(key)); 
  }
  private void setInt (String key, int value) {
    properties_.setProperty(key, String.valueOf(value));
  }
  private boolean getBoolean (String key) {
    return "true".equals(properties_.getProperty(key));
  }
  private void setBoolean (String key, boolean value) {
    properties_.setProperty(key, String.valueOf(value));
  }

  FollowAppAttributes getDefaultAttributes () throws IOException {
    if (defaultAttributes_ == null) {
      defaultAttributes_ = new FollowAppAttributes(getDefaultProperties());
      // Check for the unlikely possibility that the default font is
      // unavailable
      Font defaultFont = defaultAttributes_.getFont();
      String[] availableFontFamilyNames =
GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
      boolean defaultFontIsAvailable = false;
      for (int i=0; i < availableFontFamilyNames.length; i++) {
        if (defaultFont.getFamily().equals(availableFontFamilyNames[i])) {
          defaultFontIsAvailable = true;
          break;
        }
      }
      if (!defaultFontIsAvailable) { 
        System.out.println(
"Font family " + defaultFont.getFamily() + " is unavailable; using " + 
availableFontFamilyNames[0] + " instead."
        );
        defaultAttributes_.setFont(new Font(
          availableFontFamilyNames[0], 
          defaultFont.getStyle(), 
          defaultFont.getSize()
        ));
      }
    }
    return defaultAttributes_;
  }
  
  private EnumeratedProperties getDefaultProperties () throws IOException {
    if (defaultProperties_ == null) {
      BufferedInputStream bis = new BufferedInputStream(
        this.getClass().getResourceAsStream(propertyPrototypeFileName)
      );
      BufferedOutputStream bos = new BufferedOutputStream(
        new FileOutputStream(propertyFile)
      );
      byte[] byteArray = new byte[bufferSize];
      int len;
      while ((len = bis.read(byteArray, 0, bufferSize)) > 0) {
        bos.write(byteArray, 0, len);
      }      
      bos.flush();
      bos.close();
      bis.close();
      defaultProperties_ = new EnumeratedProperties();
      defaultProperties_.load(new BufferedInputStream(
        new FileInputStream(propertyFile)
      ));
    }
    return defaultProperties_;
  }
  
  EnumeratedProperties properties_;
  private EnumeratedProperties defaultProperties_;
  private FollowAppAttributes defaultAttributes_;

  static final String userHome = System.getProperty("user.home");
  static final String propertyFileName = 
    userHome + FollowApp.fileSeparator + ".followApp.properties";
  static final File propertyFile = new File(propertyFileName);
  static final String propertyPrototypeFileName = 
    "followApp.properties.prototype";
  static final int bufferSize = 32768;
  static final String heightKey = "height";
  static final String widthKey = "width";
  static final String xKey = "x";
  static final String yKey = "y";
  static final String followedFilesKey = "followedFiles";
  static final String tabPlacementKey = "tabs.placement";
  static final String selectedTabIndexKey = "tabs.selectedIndex";
  static final String lastFileChooserDirKey = "fileChooser.lastDir";
  static final String bufferSizeKey = "bufferSize";
  static final String latencyKey = "latency";
  static final String attributesVersionKey = "attributesVersion";
  static final String fontFamilyKey = "fontFamily";
  static final String fontStyleKey = "fontStyle";
  static final String fontSizeKey = "fontSize";
  static final String confirmDeleteKey = "confirmDelete";
  static final String confirmDeleteAllKey = "confirmDeleteAll";
  static final String autoScrollKey = "autoScroll";

  // Versions
  static final int UNVERSIONED = 0;
  static final int v1_1 = 1;
  static final int v1_2 = 2;
  static final int v1_3 = 3;
  static final int v1_3_2 = 4;
  static final int v1_4 = 5;

}

class NoTabSelectedException extends RuntimeException {} 

