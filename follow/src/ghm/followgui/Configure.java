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
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
Action which brings up a dialog allowing one to configure the Follow 
application.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class Configure extends FollowAppAction {
  
  Configure (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.Configure.name"),
      app.resBundle_.getString("action.Configure.mnemonic"),
      app.resBundle_.getString("action.Configure.accelerator"),
      app.resBundle_.getString("action.Configure.icon")
    );    
  }

  public void actionPerformed (ActionEvent e) {
    app_.setCursor(Cursor.WAIT_CURSOR);
    if (dialog_ == null) { dialog_ = new CfgDialog(); }     
    dialog_.bufferSize_.setText(
      String.valueOf(app_.attributes_.getBufferSize())
    );
    dialog_.latency_.setText(String.valueOf(app_.attributes_.getLatency()));
    dialog_.tabPlacement_.setSelectedItem(
      new TabPlacementValue(app_.attributes_.getTabPlacement())
    );
    dialog_.confirmDelete_.setValue(app_.attributes_.confirmDelete());
    dialog_.confirmDeleteAll_.setValue(app_.attributes_.confirmDeleteAll());
    dialog_.autoScroll_.setValue(app_.attributes_.autoScroll());
    dialog_.fontSelectionPanel_.setSelectedFont(app_.attributes_.getFont());
    // Quasi-kludge to get around font repainting issue
    dialog_.setLocationRelativeTo(app_.frame_);
    dialog_.setLocation(30, 30);
    // No need to set font; this is taken care of during CfgDialog construction
    dialog_.pack();
    dialog_.show();
    app_.setCursor(Cursor.DEFAULT_CURSOR);
  }
  
  CfgDialog dialog_ = null;
  
  class CfgDialog extends JDialog {
    
    CfgDialog () {
      super(
        Configure.this.app_.frame_, 
        Configure.this.app_.resBundle_.getString("dialog.Configure.title"), 
        true
      );
      JComponent contentPane = (JComponent)getContentPane();
      contentPane.setBorder(
        BorderFactory.createEmptyBorder(12, 12, 11, 11)
      );
    
      JPanel configPanel = new JPanel(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.WEST;
      gbc.ipadx = 4;
      
      // buffer size
      gbc.gridy = 0;
      configPanel.add(
  new JLabel(app_.resBundle_.getString("dialog.Configure.bufferSize.label")),
  gbc
      );
      bufferSize_ = new JTextField();
      bufferSize_.setHorizontalAlignment(JTextField.RIGHT);
      gbc.gridx = 1;      
      gbc.weightx = 1;
      gbc.ipadx = 0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      configPanel.add(bufferSize_, gbc);
      JButton bufferSizeInfo = new WhatIsThis(
        app_,
        app_.resBundle_.getString("WhatIsThis.bufferSize.title"),
        app_.resBundle_.getString("WhatIsThis.bufferSize.text")
      );
      gbc.gridx = 2;
      gbc.weightx = 0; 
      gbc.fill = GridBagConstraints.NONE;      
      configPanel.add(bufferSizeInfo, gbc);

      // latency
      gbc.gridx = 0;
      gbc.gridy++;
      gbc.ipadx = 4;
      configPanel.add(
  new JLabel(app_.resBundle_.getString("dialog.Configure.latency.label")),
  gbc
      );
      latency_ = new JTextField();
      latency_.setHorizontalAlignment(JTextField.RIGHT);
      gbc.gridx = 1;
      gbc.weightx = 1;
      gbc.ipadx = 0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      configPanel.add(latency_, gbc);
      JButton latencyInfo = new WhatIsThis(
        app_,
        app_.resBundle_.getString("WhatIsThis.latency.title"),
        app_.resBundle_.getString("WhatIsThis.latency.text")
      );
      gbc.gridx = 2;
      gbc.weightx = 0;
      gbc.fill = GridBagConstraints.NONE;
      configPanel.add(latencyInfo, gbc);

      // tab placement
      gbc.gridx = 0;
      gbc.gridy++;
      gbc.ipadx = 4;
      configPanel.add(
  new JLabel(app_.resBundle_.getString("dialog.Configure.tabPlacement.label")),
  gbc
      );
      tabPlacement_ = new JComboBox(ALL_TAB_PLACEMENT_VALUES);
      gbc.gridx = 1;
      gbc.weightx = 1;
      gbc.ipadx = 0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      configPanel.add(tabPlacement_, gbc);
      JButton tabPlacementInfo = new WhatIsThis(
        app_,
        app_.resBundle_.getString("WhatIsThis.tabPlacement.title"),
        app_.resBundle_.getString("WhatIsThis.tabPlacement.text")
      );
      gbc.gridx = 2;
      gbc.weightx = 0;
      gbc.fill = GridBagConstraints.NONE;
      configPanel.add(tabPlacementInfo, gbc);

      // confirm delete
      gbc.gridx = 0;
      gbc.gridy++;
      gbc.ipadx = 4;
      configPanel.add(
  new JLabel(app_.resBundle_.getString("dialog.Configure.confirmDelete.label")),
  gbc
      );
      confirmDelete_ = new BooleanComboBox(
app_.resBundle_.getString("dialog.Configure.confirmDelete.yes.displayValue"),
app_.resBundle_.getString("dialog.Configure.confirmDelete.no.displayValue")
      );
      gbc.gridx = 1;
      gbc.weightx = 1;
      gbc.ipadx = 0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      configPanel.add(confirmDelete_, gbc);
      JButton confirmDeleteInfo = new WhatIsThis(
        app_,
        app_.resBundle_.getString("WhatIsThis.confirmDelete.title"),
        app_.resBundle_.getString("WhatIsThis.confirmDelete.text")
      );
      gbc.gridx = 2;
      gbc.weightx = 0;
      gbc.fill = GridBagConstraints.NONE;
      configPanel.add(confirmDeleteInfo, gbc);
      
      // confirm delete all
      gbc.gridx = 0;
      gbc.gridy++;
      gbc.ipadx = 4;
      configPanel.add(
  new JLabel(
    app_.resBundle_.getString("dialog.Configure.confirmDeleteAll.label")
  ),
  gbc
      );
      confirmDeleteAll_ = new BooleanComboBox(
app_.resBundle_.getString("dialog.Configure.confirmDeleteAll.yes.displayValue"),
app_.resBundle_.getString("dialog.Configure.confirmDeleteAll.no.displayValue")
      );
      gbc.gridx = 1;
      gbc.weightx = 1;
      gbc.ipadx = 0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      configPanel.add(confirmDeleteAll_, gbc);
      JButton confirmDeleteAllInfo = new WhatIsThis(
        app_,
        app_.resBundle_.getString("WhatIsThis.confirmDeleteAll.title"),
        app_.resBundle_.getString("WhatIsThis.confirmDeleteAll.text")
      );
      gbc.gridx = 2;
      gbc.weightx = 0;
      gbc.fill = GridBagConstraints.NONE;
      configPanel.add(confirmDeleteAllInfo, gbc);

      // autoscroll
      gbc.gridx = 0;
      gbc.gridy++;
      gbc.ipadx = 4;
      configPanel.add(
  new JLabel(
    app_.resBundle_.getString("dialog.Configure.autoScroll.label")
  ),
  gbc
      );
      autoScroll_ = new BooleanComboBox(
app_.resBundle_.getString("dialog.Configure.autoScroll.yes.displayValue"),
app_.resBundle_.getString("dialog.Configure.autoScroll.no.displayValue")
      );
      gbc.gridx = 1;
      gbc.weightx = 1;
      gbc.ipadx = 0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      configPanel.add(autoScroll_, gbc);
      JButton autoScrollInfo = new WhatIsThis(
        app_,
        app_.resBundle_.getString("WhatIsThis.autoScroll.title"),
        app_.resBundle_.getString("WhatIsThis.autoScroll.text")
      );
      gbc.gridx = 2;
      gbc.weightx = 0;
      gbc.fill = GridBagConstraints.NONE;
      configPanel.add(autoScrollInfo, gbc);

      // font selection
      fontSelectionPanel_ = new CfgFontSelectionPanel();
      // Must change border to top=0 because of default top in titled border
      fontSelectionPanel_.setBorder(
        BorderFactory.createEmptyBorder(0, 12, 11, 11)
      );
      JPanel fontPanelHolder = new JPanel(new BorderLayout());
      fontPanelHolder.add(fontSelectionPanel_, BorderLayout.CENTER);
      fontPanelHolder.setBorder(BorderFactory.createTitledBorder(
        app_.resBundle_.getString("dialog.Configure.font.label")
      ));
      gbc.gridx = 0;
      gbc.gridy++;
      gbc.gridwidth = 3;
      gbc.fill = GridBagConstraints.BOTH;
      configPanel.add(fontPanelHolder, gbc);
      
      contentPane.add(configPanel, BorderLayout.CENTER);
    
      // Save button
      JButton save = new JButton(
        app_.resBundle_.getString("dialog.Configure.save.label")
      );
      save.setMnemonic(
        app_.resBundle_.getString("dialog.Configure.save.mnemonic").charAt(0)
      );
      save.addActionListener(new ActionListener () {
        public void actionPerformed (ActionEvent e) {
          // Validate fields
          StringBuffer invalidFieldsMessage = new StringBuffer();
          if (!isPositiveInteger(bufferSize_.getText())) {
            invalidFieldsMessage.append(
  app_.resBundle_.getString("dialog.Configure.bufferSizeInvalid.text")
            );
            invalidFieldsMessage.append(FollowApp.messageLineSeparator);
            invalidFieldsMessage.append(FollowApp.messageLineSeparator);
          }
          if (!isPositiveInteger(latency_.getText())) {
            invalidFieldsMessage.append(
  app_.resBundle_.getString("dialog.Configure.latencyInvalid.text")
            );
            invalidFieldsMessage.append(FollowApp.messageLineSeparator);
            invalidFieldsMessage.append(FollowApp.messageLineSeparator);
          }
          try { fontSelectionPanel_.getSelectedFont(); }
          catch (FontSelectionPanel.InvalidFontException ife) {
            invalidFieldsMessage.append(
  app_.resBundle_.getString("dialog.Configure.fontInvalid.text")
            );
            invalidFieldsMessage.append(FollowApp.messageLineSeparator);
            invalidFieldsMessage.append(FollowApp.messageLineSeparator);
          }

          if (invalidFieldsMessage.length() > 0) {
            JOptionPane.showMessageDialog(
  app_.frame_,
  invalidFieldsMessage.toString(),
  app_.resBundle_.getString("dialog.Configure.invalidFieldsDialog.title"),
  JOptionPane.ERROR_MESSAGE
            );
          }
          else {
            app_.attributes_.setBufferSize(bufferSize_.getText());
            app_.attributes_.setLatency(latency_.getText());
            app_.attributes_.setTabPlacement(
              ((TabPlacementValue)tabPlacement_.getSelectedItem()).value_
            );
            app_.attributes_.setConfirmDelete(confirmDelete_.getValue());
            app_.attributes_.setConfirmDeleteAll(confirmDeleteAll_.getValue());
            app_.attributes_.setAutoScroll(autoScroll_.getValue());
            Font selectedFont;
            try { selectedFont = fontSelectionPanel_.getSelectedFont(); }
            catch (FontSelectionPanel.InvalidFontException ife) {
              // This shouldn't happen if the error catching at the beginning
              // of actionPerformed() worked correctly
              throw new RuntimeException(
"Programmatic error; supposedly impossible scenario has occurred."
              );
            }
            app_.attributes_.setFont(selectedFont);
            Iterator followers = 
              app_.fileToFollowingPaneMap_.values().iterator();
            FileFollowingPane pane;
            while (followers.hasNext()) {
              pane = (FileFollowingPane)followers.next();
              pane.getFileFollower().setBufferSize(
                app_.attributes_.getBufferSize()
              );
              pane.getFileFollower().setLatency(app_.attributes_.getLatency());
              pane.getTextArea().setFont(selectedFont);
              pane.setAutoPositionCaret(app_.attributes_.autoScroll());
              app_.tabbedPane_.invalidate();
              app_.tabbedPane_.repaint();
            }
            app_.tabbedPane_.setTabPlacement(
              app_.attributes_.getTabPlacement()
            );
            app_.tabbedPane_.invalidate();
          }
        }
      });
      
      // Restore Defaults button
      JButton restoreDefaults = new JButton(
        app_.resBundle_.getString("dialog.Configure.restoreDefaults.label")
      );
      restoreDefaults.setMnemonic(app_.resBundle_.
        getString("dialog.Configure.restoreDefaults.mnemonic").charAt(0)
      );
      restoreDefaults.addActionListener(new ActionListener () {
        public void actionPerformed (ActionEvent e) {
          try {
  bufferSize_.setText(
    String.valueOf(app_.attributes_.getDefaultAttributes().getBufferSize())
  );
  latency_.setText(
    String.valueOf(app_.attributes_.getDefaultAttributes().getLatency())
  );
  tabPlacement_.setSelectedItem(new TabPlacementValue(
    app_.attributes_.getDefaultAttributes().getTabPlacement()
  ));
  confirmDelete_.setValue(
    app_.attributes_.getDefaultAttributes().confirmDelete()
  );
  confirmDeleteAll_.setValue(
    app_.attributes_.getDefaultAttributes().confirmDeleteAll()
  );
  autoScroll_.setValue(
    app_.attributes_.getDefaultAttributes().autoScroll()
  );
  fontSelectionPanel_.setSelectedFont(
    app_.attributes_.getDefaultAttributes().getFont()
  );
          }
          catch (IOException ioe) {
            JOptionPane.showMessageDialog(
  app_.frame_,
  app_.resBundle_.getString("dialog.Configure.cantRestoreDefaults.text"),
  app_.resBundle_.getString("dialog.Configure.cantRestoreDefaults.title"),
  JOptionPane.ERROR_MESSAGE
            );
          }
        }
      });

      // Close button
      JButton close = new JButton(
        app_.resBundle_.getString("dialog.Configure.close.label")
      );
      close.setMnemonic(
        app_.resBundle_.getString("dialog.Configure.close.mnemonic").charAt(0)
      );
      close.addActionListener(new ActionListener () {
        public void actionPerformed (ActionEvent e) { dispose(); }
      });

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      buttonPanel.add(save);
      buttonPanel.add(restoreDefaults);
      buttonPanel.add(close);
      contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private boolean isPositiveInteger (String value) {
      try {
        int intValue = Integer.parseInt(value); 
        if (intValue < 1) { return false; }
        return true;
      }
      catch (NumberFormatException nfe) { return false; }
    }
  
    JTextField bufferSize_;
    JTextField latency_;
    JComboBox tabPlacement_;
    BooleanComboBox confirmDelete_;
    BooleanComboBox confirmDeleteAll_;
    BooleanComboBox autoScroll_;
    CfgFontSelectionPanel fontSelectionPanel_;
  }

  private class TabPlacementValue {
    public TabPlacementValue (int value) {
      value_ = value;
      switch (value) {
        case JTabbedPane.TOP : 
          displayValue_ = app_.resBundle_.getString(
            "dialog.Configure.tabPlacement.Top.displayValue"
          );
          break;
        case JTabbedPane.BOTTOM : 
          displayValue_ = app_.resBundle_.getString(
            "dialog.Configure.tabPlacement.Bottom.displayValue"
          );
          break;
        case JTabbedPane.LEFT :
          displayValue_ = app_.resBundle_.getString(
            "dialog.Configure.tabPlacement.Left.displayValue"
          );
          break;
        case JTabbedPane.RIGHT :
          displayValue_ = app_.resBundle_.getString(
            "dialog.Configure.tabPlacement.Right.displayValue"
          );
          break;
        default : 
          throw new IllegalArgumentException (
            "int value must be one of the tab placement values from JTabbedPane"
          );
      }
    }
    public int value_;
    public String displayValue_;
    public String toString () { return displayValue_; }
    public boolean equals (Object o) {
      if (o.getClass() == getClass()) {
        return value_ == ((TabPlacementValue)o).value_;
      }
      return false;
    }
  }
  
  private class CfgFontSelectionPanel extends FontSelectionPanel {
    CfgFontSelectionPanel () {
      super(
        Configure.this.app_.attributes_.getFont(),
        getStyleDisplayValues(Configure.this.app_.resBundle_),
        new int[] {8, 9, 10, 12, 14}
      );
      this.fontFamilyList_.setVisibleRowCount(5);
    }
  }
  private static String[] getStyleDisplayValues (ResourceBundle bundle) {
    return new String[] {
bundle.getString("dialog.Configure.font.plain.displayValue"),
bundle.getString("dialog.Configure.font.bold.displayValue"),
bundle.getString("dialog.Configure.font.italic.displayValue"),
bundle.getString("dialog.Configure.font.boldItalic.displayValue")
    };
  }
  
  private TabPlacementValue TOP = new TabPlacementValue(JTabbedPane.TOP);
  private TabPlacementValue BOTTOM = new TabPlacementValue(JTabbedPane.BOTTOM);
  private TabPlacementValue LEFT = new TabPlacementValue(JTabbedPane.LEFT);
  private TabPlacementValue RIGHT = new TabPlacementValue(JTabbedPane.RIGHT);
  private TabPlacementValue[] ALL_TAB_PLACEMENT_VALUES = 
    new TabPlacementValue [] { TOP, BOTTOM, LEFT, RIGHT } ;

  static class BooleanComboBox extends JComboBox {
    BooleanComboBox (String trueDisplayValue, String falseDisplayValue) {
      super(new String[] {trueDisplayValue, falseDisplayValue});
    }
    public void setValue (boolean value) {
      if (value == true) { this.setSelectedIndex(0); }
      else { this.setSelectedIndex(1); }
    }
    public boolean getValue () {
      return (this.getSelectedIndex() == 0);
    }
  }
    
}

