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

import ghm.follow.font.FontSelectionPanel;
import ghm.follow.font.InvalidFontException;
import ghm.follow.gui.FileFollowingPane;
import ghm.follow.gui.FollowApp;
import ghm.follow.gui.FollowAppAction;
import ghm.follow.gui.WhatIsThis;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Action which brings up a dialog allowing one to configure the Follow
 * application.
 * 
 * @author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
public class Configure extends FollowAppAction {
	public static final String NAME = "configure";

	public Configure(FollowApp app) {
		super(app, app.getResourceBundle().getString("action.Configure.name"), app
				.getResourceBundle().getString("action.Configure.mnemonic"), app
				.getResourceBundle().getString("action.Configure.accelerator"), app
				.getResourceBundle().getString("action.Configure.icon"));
	}

	public void actionPerformed(ActionEvent e) {
		getApp().setCursor(Cursor.WAIT_CURSOR);
		if (dialog_ == null) {
			dialog_ = new CfgDialog();
		}
		dialog_.bufferSize_.setText(String.valueOf(getApp().getAttributes().getBufferSize()));
		dialog_.latency_.setText(String.valueOf(getApp().getAttributes().getLatency()));
		dialog_.tabPlacement_.setSelectedItem(new TabPlacementValue(getApp().getAttributes()
				.getTabPlacement()));
		dialog_.confirmDelete_.setValue(getApp().getAttributes().confirmDelete());
		dialog_.confirmDeleteAll_.setValue(getApp().getAttributes().confirmDeleteAll());
		dialog_.autoScroll_.setValue(getApp().getAttributes().autoScroll());
		dialog_.editor_.setText(String.valueOf(getApp().getAttributes().getEditor()));
		dialog_.tabSize_.setText(String.valueOf(getApp().getAttributes().getTabSize()));
		dialog_.fontSelectionPanel_.setSelectedFont(getApp().getAttributes().getFont());
		dialog_.recentFilesMax_.setText(String
				.valueOf(getApp().getAttributes().getRecentFilesMax()));
		// Quasi-kludge to get around font repainting issue
		dialog_.setLocationRelativeTo(getApp().getFrame());
		dialog_.setLocation(30, 30);
		// No need to set font; this is taken care of during CfgDialog
		// construction
		dialog_.pack();
		dialog_.setVisible(true);
		getApp().setCursor(Cursor.DEFAULT_CURSOR);
	}

	private CfgDialog dialog_;

	class CfgDialog extends JDialog {
		protected JRootPane createRootPane() {
			KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
			JRootPane rootPane = new JRootPane();
			rootPane.registerKeyboardAction(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					dialog_.close_.doClick();
				}
			}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
			return rootPane;
		}

		CfgDialog() {
			super(Configure.this.getApp().getFrame(), Configure.this.getApp().getResourceBundle()
					.getString("dialog.Configure.title"), true);
			JComponent contentPane = (JComponent) getContentPane();
			contentPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));

			JPanel configPanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.ipadx = 4;

			// buffer size
			gbc.gridy = 0;
			configPanel.add(new JLabel(getApp().getResourceBundle().getString(
					"dialog.Configure.bufferSize.label")), gbc);
			bufferSize_ = new JTextField();
			bufferSize_.setHorizontalAlignment(JTextField.RIGHT);
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.ipadx = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			configPanel.add(bufferSize_, gbc);
			JButton bufferSizeInfo = new WhatIsThis(getApp(), getApp().getResourceBundle()
					.getString("WhatIsThis.bufferSize.title"), getApp().getResourceBundle()
					.getString("WhatIsThis.bufferSize.text"));
			gbc.gridx = 2;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			configPanel.add(bufferSizeInfo, gbc);

			// latency
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.ipadx = 4;
			configPanel.add(new JLabel(getApp().getResourceBundle().getString(
					"dialog.Configure.latency.label")), gbc);
			latency_ = new JTextField();
			latency_.setHorizontalAlignment(JTextField.RIGHT);
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.ipadx = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			configPanel.add(latency_, gbc);
			JButton latencyInfo = new WhatIsThis(getApp(), getApp().getResourceBundle().getString(
					"WhatIsThis.latency.title"), getApp().getResourceBundle().getString(
					"WhatIsThis.latency.text"));
			gbc.gridx = 2;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			configPanel.add(latencyInfo, gbc);

			// tab placement
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.ipadx = 4;
			configPanel.add(new JLabel(getApp().getResourceBundle().getString(
					"dialog.Configure.tabPlacement.label")), gbc);
			tabPlacement_ = new JComboBox(ALL_TAB_PLACEMENT_VALUES);
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.ipadx = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			configPanel.add(tabPlacement_, gbc);
			JButton tabPlacementInfo = new WhatIsThis(getApp(), getApp().getResourceBundle()
					.getString("WhatIsThis.tabPlacement.title"), getApp().getResourceBundle()
					.getString("WhatIsThis.tabPlacement.text"));
			gbc.gridx = 2;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			configPanel.add(tabPlacementInfo, gbc);

			// confirm delete
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.ipadx = 4;
			configPanel.add(new JLabel(getApp().getResourceBundle().getString(
					"dialog.Configure.confirmDelete.label")), gbc);
			confirmDelete_ = new BooleanComboBox(getApp().getResourceBundle().getString(
					"dialog.Configure.confirmDelete.yes.displayValue"), getApp()
					.getResourceBundle()
					.getString("dialog.Configure.confirmDelete.no.displayValue"));
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.ipadx = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			configPanel.add(confirmDelete_, gbc);
			JButton confirmDeleteInfo = new WhatIsThis(getApp(), getApp().getResourceBundle()
					.getString("WhatIsThis.confirmDelete.title"), getApp().getResourceBundle()
					.getString("WhatIsThis.confirmDelete.text"));
			gbc.gridx = 2;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			configPanel.add(confirmDeleteInfo, gbc);

			// confirm delete all
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.ipadx = 4;
			configPanel.add(new JLabel(getApp().getResourceBundle().getString(
					"dialog.Configure.confirmDeleteAll.label")), gbc);
			confirmDeleteAll_ = new BooleanComboBox(getApp().getResourceBundle().getString(
					"dialog.Configure.confirmDeleteAll.yes.displayValue"), getApp()
					.getResourceBundle().getString(
							"dialog.Configure.confirmDeleteAll.no.displayValue"));
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.ipadx = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			configPanel.add(confirmDeleteAll_, gbc);
			JButton confirmDeleteAllInfo = new WhatIsThis(getApp(), getApp().getResourceBundle()
					.getString("WhatIsThis.confirmDeleteAll.title"), getApp().getResourceBundle()
					.getString("WhatIsThis.confirmDeleteAll.text"));
			gbc.gridx = 2;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			configPanel.add(confirmDeleteAllInfo, gbc);

			// autoscroll
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.ipadx = 4;
			configPanel.add(new JLabel(getApp().getResourceBundle().getString(
					"dialog.Configure.autoScroll.label")), gbc);
			autoScroll_ = new BooleanComboBox(getApp().getResourceBundle().getString(
					"dialog.Configure.autoScroll.yes.displayValue"), getApp().getResourceBundle()
					.getString("dialog.Configure.autoScroll.no.displayValue"));
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.ipadx = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			configPanel.add(autoScroll_, gbc);
			JButton autoScrollInfo = new WhatIsThis(getApp(), getApp().getResourceBundle()
					.getString("WhatIsThis.autoScroll.title"), getApp().getResourceBundle()
					.getString("WhatIsThis.autoScroll.text"));
			gbc.gridx = 2;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			configPanel.add(autoScrollInfo, gbc);

			// external editor
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.ipadx = 4;
			configPanel.add(new JLabel(getApp().getResourceBundle().getString(
					"dialog.Configure.editor.label")), gbc);
			editor_ = new JTextField();
			editor_.setHorizontalAlignment(JTextField.LEFT);
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.ipadx = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			configPanel.add(editor_, gbc);
			JButton editorInfo = new WhatIsThis(getApp(), getApp().getResourceBundle().getString(
					"WhatIsThis.editor.title"), getApp().getResourceBundle().getString(
					"WhatIsThis.editor.text"));
			gbc.gridx = 2;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			configPanel.add(editorInfo, gbc);

			// tabSize
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.ipadx = 4;
			configPanel.add(new JLabel(getApp().getResourceBundle().getString(
					"dialog.Configure.tabSize.label")), gbc);
			tabSize_ = new JTextField();
			tabSize_.setHorizontalAlignment(JTextField.RIGHT);
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.ipadx = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			configPanel.add(tabSize_, gbc);
			JButton tabSizeInfo = new WhatIsThis(getApp(), getApp().getResourceBundle().getString(
					"WhatIsThis.tabSize.title"), getApp().getResourceBundle().getString(
					"WhatIsThis.tabSize.text"));
			gbc.gridx = 2;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			configPanel.add(tabSizeInfo, gbc);

			// recentFilesMax
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.ipadx = 4;
			configPanel.add(new JLabel(getApp().getResourceBundle().getString(
					"dialog.Configure.recentFilesMax.label")), gbc);
			recentFilesMax_ = new JTextField();
			recentFilesMax_.setHorizontalAlignment(JTextField.RIGHT);
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.ipadx = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			configPanel.add(recentFilesMax_, gbc);
			JButton recentFilesMaxInfo = new WhatIsThis(getApp(), getApp().getResourceBundle()
					.getString("WhatIsThis.recentFilesMax.title"), getApp().getResourceBundle()
					.getString("WhatIsThis.recentFilesMax.text"));
			gbc.gridx = 2;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			configPanel.add(recentFilesMaxInfo, gbc);

			// font selection
			fontSelectionPanel_ = new CfgFontSelectionPanel();
			// Must change border to top=0 because of default top in titled
			// border
			fontSelectionPanel_.setBorder(BorderFactory.createEmptyBorder(0, 12, 11, 11));
			JPanel fontPanelHolder = new JPanel(new BorderLayout());
			fontPanelHolder.add(fontSelectionPanel_, BorderLayout.CENTER);
			fontPanelHolder.setBorder(BorderFactory.createTitledBorder(getApp().getResourceBundle()
					.getString("dialog.Configure.font.label")));
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.gridwidth = 3;
			gbc.fill = GridBagConstraints.BOTH;
			configPanel.add(fontPanelHolder, gbc);

			contentPane.add(configPanel, BorderLayout.CENTER);

			// Save button
			save_ = new JButton(getApp().getResourceBundle().getString(
					"dialog.Configure.save.label"));
			save_.setMnemonic(getApp().getResourceBundle().getString(
					"dialog.Configure.save.mnemonic").charAt(0));
			save_.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Validate fields
					StringBuffer invalidFieldsMessage = new StringBuffer();
					if (!isPositiveInteger(bufferSize_.getText())) {
						invalidFieldsMessage.append(getApp().getResourceBundle().getString(
								"dialog.Configure.bufferSizeInvalid.text"));
						invalidFieldsMessage.append(FollowApp.MESSAGE_LINE_SEPARATOR);
						invalidFieldsMessage.append(FollowApp.MESSAGE_LINE_SEPARATOR);
					}
					if (!isPositiveInteger(latency_.getText())) {
						invalidFieldsMessage.append(getApp().getResourceBundle().getString(
								"dialog.Configure.latencyInvalid.text"));
						invalidFieldsMessage.append(FollowApp.MESSAGE_LINE_SEPARATOR);
						invalidFieldsMessage.append(FollowApp.MESSAGE_LINE_SEPARATOR);
					}
					if (!isPositiveInteger(recentFilesMax_.getText())) {
						invalidFieldsMessage.append(getApp().getResourceBundle().getString(
								"dialog.Configure.recentFilesMaxInvalid.text"));
						invalidFieldsMessage.append(FollowApp.MESSAGE_LINE_SEPARATOR);
						invalidFieldsMessage.append(FollowApp.MESSAGE_LINE_SEPARATOR);
					}
					try {
						fontSelectionPanel_.getSelectedFont();
					}
					catch (InvalidFontException ife) {
						invalidFieldsMessage.append(getApp().getResourceBundle().getString(
								"dialog.Configure.fontInvalid.text"));
						invalidFieldsMessage.append(FollowApp.MESSAGE_LINE_SEPARATOR);
						invalidFieldsMessage.append(FollowApp.MESSAGE_LINE_SEPARATOR);
					}

					if (invalidFieldsMessage.length() > 0) {
						JOptionPane.showMessageDialog(getApp().getFrame(), invalidFieldsMessage
								.toString(), getApp().getResourceBundle().getString(
								"dialog.Configure.invalidFieldsDialog.title"),
								JOptionPane.ERROR_MESSAGE);
					}
					else {
						getApp().getAttributes().setBufferSize(bufferSize_.getText());
						getApp().getAttributes().setLatency(latency_.getText());
						getApp().getAttributes().setTabPlacement(
								((TabPlacementValue) tabPlacement_.getSelectedItem()).value_);
						getApp().getAttributes().setConfirmDelete(confirmDelete_.getValue());
						getApp().getAttributes().setConfirmDeleteAll(confirmDeleteAll_.getValue());
						getApp().getAttributes().setAutoScroll(autoScroll_.getValue());
						getApp().getAttributes().setEditor(editor_.getText());
						getApp().getAttributes().setTabSize(tabSize_.getText());
						getApp().getAttributes().setRecentFilesMax(recentFilesMax_.getText());
						getApp().refreshRecentFilesMenu();
						Font selectedFont;
						try {
							selectedFont = fontSelectionPanel_.getSelectedFont();
						}
						catch (InvalidFontException ife) {
							// This shouldn't happen if the error catching at
							// the beginning
							// of actionPerformed() worked correctly
							throw new RuntimeException(
									"Programmatic error; supposedly impossible scenario has occurred.");
						}
						getApp().getAttributes().setFont(selectedFont);
						Iterator followers = getApp().getFileToFollowingPaneMap().values()
								.iterator();
						FileFollowingPane pane;
						while (followers.hasNext()) {
							pane = (FileFollowingPane) followers.next();
							pane.getFileFollower().setBufferSize(
									getApp().getAttributes().getBufferSize());
							pane.getFileFollower()
									.setLatency(getApp().getAttributes().getLatency());
							pane.getTextArea().setFont(selectedFont);
							pane.setAutoPositionCaret(getApp().getAttributes().autoScroll());
							pane.getTextArea().setTabSize(getApp().getAttributes().getTabSize());
							getApp().getTabbedPane().invalidate();
							getApp().getTabbedPane().repaint();
						}
						getApp().getTabbedPane().setTabPlacement(
								getApp().getAttributes().getTabPlacement());
						getApp().getTabbedPane().invalidate();
					}
				}
			});

			// Restore Defaults button
			restoreDefaults_ = new JButton(getApp().getResourceBundle().getString(
					"dialog.Configure.restoreDefaults.label"));
			restoreDefaults_.setMnemonic(getApp().getResourceBundle().getString(
					"dialog.Configure.restoreDefaults.mnemonic").charAt(0));
			restoreDefaults_.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						bufferSize_.setText(String.valueOf(getApp().getAttributes()
								.getDefaultAttributes().getBufferSize()));
						latency_.setText(String.valueOf(getApp().getAttributes()
								.getDefaultAttributes().getLatency()));
						tabPlacement_.setSelectedItem(new TabPlacementValue(getApp()
								.getAttributes().getDefaultAttributes().getTabPlacement()));
						confirmDelete_.setValue(getApp().getAttributes().getDefaultAttributes()
								.confirmDelete());
						confirmDeleteAll_.setValue(getApp().getAttributes().getDefaultAttributes()
								.confirmDeleteAll());
						autoScroll_.setValue(getApp().getAttributes().getDefaultAttributes()
								.autoScroll());
						editor_.setText(String.valueOf(getApp().getAttributes()
								.getDefaultAttributes().getEditor()));
						fontSelectionPanel_.setSelectedFont(getApp().getAttributes()
								.getDefaultAttributes().getFont());
						recentFilesMax_.setText(String.valueOf(getApp().getAttributes()
								.getDefaultAttributes().getRecentFilesMax()));
					}
					catch (IOException ioe) {
						JOptionPane.showMessageDialog(getApp().getFrame(), getApp()
								.getResourceBundle().getString(
										"dialog.Configure.cantRestoreDefaults.text"), getApp()
								.getResourceBundle().getString(
										"dialog.Configure.cantRestoreDefaults.title"),
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});

			// Close button
			close_ = new JButton(getApp().getResourceBundle().getString(
					"dialog.Configure.close.label"));
			close_.setMnemonic(getApp().getResourceBundle().getString(
					"dialog.Configure.close.mnemonic").charAt(0));
			close_.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});

			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			buttonPanel.add(save_);
			buttonPanel.add(restoreDefaults_);
			buttonPanel.add(close_);
			contentPane.add(buttonPanel, BorderLayout.SOUTH);
		}

		private boolean isPositiveInteger(String value) {
			try {
				int intValue = Integer.parseInt(value);
				if (intValue < 1) {
					return false;
				}
				return true;
			}
			catch (NumberFormatException nfe) {
				return false;
			}
		}

		JTextField bufferSize_;
		JTextField latency_;
		JComboBox tabPlacement_;
		BooleanComboBox confirmDelete_;
		BooleanComboBox confirmDeleteAll_;
		BooleanComboBox autoScroll_;
		JTextField editor_;
		JTextField tabSize_;
		JTextField recentFilesMax_;
		CfgFontSelectionPanel fontSelectionPanel_;
		JButton save_;
		JButton restoreDefaults_;
		JButton close_;
	}

	private class TabPlacementValue {
		public TabPlacementValue(int value) {
			value_ = value;
			switch (value) {
			case JTabbedPane.TOP:
				displayValue_ = getApp().getResourceBundle().getString(
						"dialog.Configure.tabPlacement.Top.displayValue");
				break;
			case JTabbedPane.BOTTOM:
				displayValue_ = getApp().getResourceBundle().getString(
						"dialog.Configure.tabPlacement.Bottom.displayValue");
				break;
			case JTabbedPane.LEFT:
				displayValue_ = getApp().getResourceBundle().getString(
						"dialog.Configure.tabPlacement.Left.displayValue");
				break;
			case JTabbedPane.RIGHT:
				displayValue_ = getApp().getResourceBundle().getString(
						"dialog.Configure.tabPlacement.Right.displayValue");
				break;
			default:
				throw new IllegalArgumentException(
						"int value must be one of the tab placement values from JTabbedPane");
			}
		}

		public int value_;

		public String displayValue_;

		public String toString() {
			return displayValue_;
		}

		public boolean equals(Object o) {
			if (o.getClass() == getClass()) {
				return value_ == ((TabPlacementValue) o).value_;
			}
			return false;
		}
	}

	private class CfgFontSelectionPanel extends FontSelectionPanel {
		CfgFontSelectionPanel() {
			super(Configure.this.getApp().getAttributes().getFont(),
					getStyleDisplayValues(Configure.this.getApp().getResourceBundle()), new int[] {
							8, 9, 10, 12, 14 });
			this.fontFamilyList_.setVisibleRowCount(5);
		}
	}

	private static String[] getStyleDisplayValues(ResourceBundle bundle) {
		return new String[] { bundle.getString("dialog.Configure.font.plain.displayValue"),
				bundle.getString("dialog.Configure.font.bold.displayValue"),
				bundle.getString("dialog.Configure.font.italic.displayValue"),
				bundle.getString("dialog.Configure.font.boldItalic.displayValue") };
	}

	private TabPlacementValue TOP = new TabPlacementValue(JTabbedPane.TOP);

	private TabPlacementValue BOTTOM = new TabPlacementValue(JTabbedPane.BOTTOM);

	private TabPlacementValue LEFT = new TabPlacementValue(JTabbedPane.LEFT);

	private TabPlacementValue RIGHT = new TabPlacementValue(JTabbedPane.RIGHT);

	private TabPlacementValue[] ALL_TAB_PLACEMENT_VALUES = new TabPlacementValue[] { TOP, BOTTOM,
			LEFT, RIGHT };

	static class BooleanComboBox extends JComboBox {
		BooleanComboBox(String trueDisplayValue, String falseDisplayValue) {
			super(new String[] { trueDisplayValue, falseDisplayValue });
		}

		public void setValue(boolean value) {
			if (value == true) {
				this.setSelectedIndex(0);
			}
			else {
				this.setSelectedIndex(1);
			}
		}

		public boolean getValue() {
			return (this.getSelectedIndex() == 0);
		}
	}
}