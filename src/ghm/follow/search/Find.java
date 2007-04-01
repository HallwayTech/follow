package ghm.follow.search;

import ghm.follow.gui.FileFollowingPane;
import ghm.follow.gui.FollowApp;
import ghm.follow.gui.FollowAppAction;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Find extends FollowAppAction {
	public static final String NAME = "find";

	private FindDialog _dialog;
	private JTextField _find;
	private JCheckBox _regEx;
	private JCheckBox _caseSensitive;
	private JButton _findButton;
	private JButton _clearButton;
	private JButton _closeButton;
	private JLabel _statusBar;
	private JScrollPane _resultPane;
	private String _resultsLabel;

	public Find(FollowApp app) {
		super(app, app.getResourceBundle().getString("action.Find.name"), app.getResourceBundle()
				.getString("action.Find.mnemonic"), app.getResourceBundle().getString(
				"action.Find.accelerator"));
		_resultsLabel = getApp().getResourceBundle().getString("dialog.Find.results.label").trim();
	}

	public void actionPerformed(ActionEvent e) {
		getApp().setCursor(Cursor.WAIT_CURSOR);
		if (_dialog == null) {
			_dialog = new FindDialog();
			_dialog.setLocationRelativeTo(getApp().getFrame());
			_dialog.setLocation(100, 100);
			_dialog.pack();
		}
		_find.grabFocus();
		_find.selectAll();
		_dialog.setVisible(true);
		getApp().setCursor(Cursor.DEFAULT_CURSOR);
	}

	private LineResult[] doFind() {
		// get the current selected tab
		FileFollowingPane pane = getApp().getSelectedFileFollowingPane();
		// search the tab with the given text
		SearchableTextPane textArea = pane.getTextPane();
		int flags = 0;
		
		if (_caseSensitive.isSelected()) {
			flags |= SearchEngine.CASE_SENSITIVE;
		}
		if (_regEx.isSelected()) {
			flags |= SearchEngine.REGEX;
		}
		LineResult[] results = textArea.highlight(_find.getText(), flags);
		// select search term for convenience
		_find.grabFocus();
		_find.selectAll();
		return results;
	}

	class FindDialog extends JDialog {
		FindDialog() {
			super(Find.this.getApp().getFrame(), Find.this.getApp().getResourceBundle().getString(
					"dialog.Find.title"), false);
			addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						setVisible(false);
					}
				}
			});
			setResizable(false);
			JComponent contentPane = (JComponent) getContentPane();
			contentPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			JPanel findPanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			//
			// add the find field & label
			//
			gbc.ipadx = 4;
			findPanel.add(new JLabel(getApp().getResourceBundle().getString(
					"dialog.Find.findText.label")), gbc);
			_find = new JTextField(15);
			_find.setHorizontalAlignment(JTextField.LEFT);
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.ipadx = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			findPanel.add(_find);
			//
			// add the case sensitive check box
			//
			_caseSensitive = new JCheckBox(getApp().getResourceBundle().getString(
					"dialog.Find.caseSensitive.label"));
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			_caseSensitive.setHorizontalAlignment(JCheckBox.LEFT);
			findPanel.add(_caseSensitive, gbc);
			//
			// add the regular expression check box
			//
			_regEx = new JCheckBox(getApp().getResourceBundle().getString(
					"dialog.Find.regularExpression.label"));
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			_regEx.setHorizontalAlignment(JCheckBox.LEFT);
			findPanel.add(_regEx, gbc);
			//
			// add the find button
			//
			_findButton = new JButton(getApp().getResourceBundle().getString(
					"dialog.Find.findButton.label"));
			_findButton.setMnemonic(getApp().getResourceBundle().getString(
					"dialog.Find.findButton.mnemonic").charAt(0));
			_findButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					findButton_clicked(e);
					_find.grabFocus();
					_find.selectAll();
				}
			});

			// add the clear button
			_clearButton = new JButton(getApp().getResourceBundle().getString(
					"dialog.Find.clearButton.label"));
			_clearButton.setMnemonic(getApp().getResourceBundle().getString(
					"dialog.Find.clearButton.mnemonic").charAt(0));
			_clearButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					clearButton_clicked(e);
					_find.grabFocus();
					_find.selectAll();
				}
			});

			// add the close button
			_closeButton = new JButton(getApp().getResourceBundle().getString(
					"dialog.Find.closeButton.label"));
			_closeButton.setMnemonic(getApp().getResourceBundle().getString(
					"dialog.Find.closeButton.mnemonic").charAt(0));
			_closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					_dialog.setVisible(false);
				}
			});
			// add the buttons to the dialog
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

			// create buttons
			buttonPanel.add(_findButton);
			buttonPanel.add(_clearButton);
			buttonPanel.add(_closeButton);

			// create status bar
			JPanel statusPanel = new JPanel(new BorderLayout());
			_resultPane = new JScrollPane();
			_resultPane.setVisible(false);
			_statusBar = new JLabel(" ");
			_statusBar.setFont(new Font("Arial", Font.PLAIN, 10));
			_statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
			statusPanel.add(_statusBar, BorderLayout.CENTER);
			statusPanel.add(_resultPane, BorderLayout.SOUTH);

			// add everything to the content pane
			contentPane.add(findPanel, BorderLayout.NORTH);
			contentPane.add(buttonPanel, BorderLayout.CENTER);
			contentPane.add(statusPanel, BorderLayout.SOUTH);
		}

		/**
		 * Override method to add ESCAPE key action for window close
		 * 
		 * @author chall
		 */
		protected JRootPane createRootPane() {
			KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
			JRootPane rootPane = new JRootPane();
			rootPane.registerKeyboardAction(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					_closeButton.doClick();
				}
			}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
			stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
			rootPane.registerKeyboardAction(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					_findButton.doClick();
				}
			}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
			return rootPane;
		}

		private void findButton_clicked(ActionEvent e) {
			getApp().setCursor(Cursor.WAIT_CURSOR);
			clearResults();
			LineResult[] results = doFind();
			if (results != null) {
				if (results.length == 0) {
					_statusBar.setText("Search term not found.");
				}
				else {
					JList resultList = showResults(results);
					System.out.println(resultList.getSelectionBackground());
					resultList.addListSelectionListener(new ListSelectionListener() {
						/**
						 * Catches selection events and sets the caret within
						 * the view so that the screen scrolls
						 * 
						 * @author chall
						 */
						public void valueChanged(ListSelectionEvent ev) {
							resultList_changed(ev);
						}
					});
				}
			}
			getApp().setCursor(Cursor.DEFAULT_CURSOR);
		}

		private void clearButton_clicked(ActionEvent e) {
			getApp().setCursor(Cursor.WAIT_CURSOR);
			// get the current selected tab
			FileFollowingPane pane = getApp().getSelectedFileFollowingPane();
			// clear the highlights from the searched tab
			SearchableTextPane textArea = pane.getTextPane();
			textArea.removeHighlights();
			// clear the status bar and result list
			clearResults();
			getApp().setCursor(Cursor.DEFAULT_CURSOR);
		}

		/**
		 * Show results some results by creating a list and updating the status
		 * bar
		 * 
		 * @author chall
		 * @param results
		 * @return
		 */
		private JList showResults(LineResult[] results) {
			// create a list of the results
			JList resultList = new JList(results);
			resultList.setFont(new Font("Arial", Font.PLAIN, 10));

			// set the status bar
			;
			_statusBar.setText(" " + countResults(results) + " " + _resultsLabel);

			// show the result list
			_resultPane.getViewport().setView(resultList);
			_resultPane.setVisible(true);

			// resize the dialog
			_dialog.pack();
			return resultList;
		}

		private void resultList_changed(ListSelectionEvent ev) {
			if (!ev.getValueIsAdjusting()) {
				JList list = (JList) ev.getSource();
				int pos = list.getSelectedIndex();
				if (pos >= 0) {
					// get the result associated to the
					// selected position
					LineResult result = (LineResult) list.getModel().getElementAt(pos);

					// get the current selected tab
					// and text area
					FileFollowingPane ffp = getApp().getSelectedFileFollowingPane();
					SearchableTextPane tp = ffp.getTextPane();
					// move the caret to the chosen text
					tp.setCaretPosition(result.getFirstWordPosition());
				}
			}
		}

		private int countResults(LineResult[] results) {
			int count = 0;
			for (int i = 0; i < results.length; i++) {
				count += results[i].getWordResults().length;
			}
			return count;
		}

		/**
		 * Clear out the results list and status bar.
		 * 
		 * @author chall
		 */
		private void clearResults() {
			// clear the status bar
			_statusBar.setText(" ");

			// clear and hide the result list
			_resultPane.getViewport().setView(null);
			_resultPane.setVisible(false);

			// resize the dialog
			_dialog.pack();
		}
	}
}