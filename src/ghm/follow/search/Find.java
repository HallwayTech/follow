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

	private FindDialog dialog_;

	private JTextField find_;

	private JCheckBox regularExpression_;

	private JCheckBox caseSensitive_;

	private JButton findButton_;

	private JButton clearButton_;

	private JButton closeButton_;

	private JLabel statusBar_;

	private JScrollPane resultPane_;

	public Find(FollowApp app) {
		super(app, app.getResourceBundle().getString("action.Find.name"), app.getResourceBundle()
				.getString("action.Find.mnemonic"), app.getResourceBundle().getString(
				"action.Find.accelerator"));
	}

	public void actionPerformed(ActionEvent e) {
		getApp().setCursor(Cursor.WAIT_CURSOR);
		if (dialog_ == null) {
			dialog_ = new FindDialog();
			dialog_.setLocationRelativeTo(getApp().getFrame());
			dialog_.setLocation(100, 100);
			dialog_.pack();
		}
		find_.grabFocus();
		find_.selectAll();
		dialog_.setVisible(true);
		getApp().setCursor(Cursor.DEFAULT_CURSOR);
	}

	private LineResult[] doFind() {
		// get the current selected tab
		FileFollowingPane pane = getApp().getSelectedFileFollowingPane();
		// search the tab with the given text
		SearchableTextPane textArea = pane.getTextPane();
		// textArea.selectAll();
		LineResult[] results = textArea.highlight(find_.getText(), caseSensitive_.isSelected(),
				regularExpression_.isSelected());
		if (results == null)
			results = new LineResult[0];
		// select search term for convenience
		find_.selectAll();
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
			find_ = new JTextField(15);
			find_.setHorizontalAlignment(JTextField.LEFT);
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.ipadx = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			findPanel.add(find_);
			//
			// add the case sensitive check box
			//
			caseSensitive_ = new JCheckBox(getApp().getResourceBundle().getString(
					"dialog.Find.caseSensitive.label"));
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			caseSensitive_.setHorizontalAlignment(JCheckBox.LEFT);
			findPanel.add(caseSensitive_, gbc);
			//
			// add the regular expression check box
			//
			regularExpression_ = new JCheckBox(getApp().getResourceBundle().getString(
					"dialog.Find.regularExpression.label"));
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			regularExpression_.setHorizontalAlignment(JCheckBox.LEFT);
			findPanel.add(regularExpression_, gbc);
			//
			// add the find button
			//
			findButton_ = new JButton(getApp().getResourceBundle().getString(
					"dialog.Find.findButton.label"));
			findButton_.setMnemonic(getApp().getResourceBundle().getString(
					"dialog.Find.findButton.mnemonic").charAt(0));
			findButton_.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					clearResults();
					LineResult[] results = doFind();
					if (results != null) {
						if (results.length == 0) {
							statusBar_.setText("Search term not found.");
						}
						else {
							JList resultList = showResults(results);
							resultList.addListSelectionListener(new ListSelectionListener() {
								/**
								 * Catches selection events and sets the caret
								 * within the view so that the screen scrolls
								 * 
								 * @author chall
								 */
								public void valueChanged(ListSelectionEvent ev) {
									if (!ev.getValueIsAdjusting()) {
										JList list = (JList) ev.getSource();
										int pos = list.getSelectedIndex();
										if (pos >= 0) {
											// get the result associated to the
											// selected position
											LineResult result = (LineResult) list.getModel()
													.getElementAt(pos);

											// get the current selected tab
											// and text area
											FileFollowingPane pane = getApp()
													.getSelectedFileFollowingPane();
											SearchableTextPane textArea = pane.getTextPane();
											// move the caret to the chosen text
											textArea.setCaretPosition(result.getFirstWordPosition());
										}
									}
								}
							});
						}
					}
				}
			});

			// add the clear button
			clearButton_ = new JButton(getApp().getResourceBundle().getString(
					"dialog.Find.clearButton.label"));
			clearButton_.setMnemonic(getApp().getResourceBundle().getString(
					"dialog.Find.clearButton.mnemonic").charAt(0));
			clearButton_.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// get the current selected tab
					FileFollowingPane pane = getApp().getSelectedFileFollowingPane();
					// clear the highlights from the searched tab
					SearchableTextPane textArea = pane.getTextPane();
					textArea.removeHighlights();
					// clear the status bar and result list
					clearResults();
				}
			});

			// add the close button
			closeButton_ = new JButton(getApp().getResourceBundle().getString(
					"dialog.Find.closeButton.label"));
			closeButton_.setMnemonic(getApp().getResourceBundle().getString(
					"dialog.Find.closeButton.mnemonic").charAt(0));
			closeButton_.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dialog_.setVisible(false);
				}
			});
			// add the buttons to the dialog
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

			// create buttons
			buttonPanel.add(findButton_);
			buttonPanel.add(clearButton_);
			buttonPanel.add(closeButton_);

			// create status bar
			JPanel statusPanel = new JPanel(new BorderLayout());
			resultPane_ = new JScrollPane();
			resultPane_.setVisible(false);
			statusBar_ = new JLabel(" ");
			statusBar_.setFont(new Font("Arial", Font.PLAIN, 10));
			statusBar_.setBorder(new BevelBorder(BevelBorder.LOWERED));
			statusPanel.add(statusBar_, BorderLayout.CENTER);
			statusPanel.add(resultPane_, BorderLayout.SOUTH);

			// add everything to the content pane
			contentPane.add(findPanel, BorderLayout.NORTH);
			contentPane.add(buttonPanel, BorderLayout.CENTER);
			contentPane.add(statusPanel, BorderLayout.SOUTH);
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
			statusBar_.setText(" " + results.length + " occurence(s) found.");

			// show the result list
			resultPane_.getViewport().setView(resultList);
			resultPane_.setVisible(true);

			// resize the dialog
			dialog_.pack();
			return resultList;
		}

		/**
		 * Clear out the results list and status bar.
		 * 
		 * @author chall
		 */
		private void clearResults() {
			// clear the status bar
			statusBar_.setText(" ");

			// clear and hide the result list
			resultPane_.getViewport().setView(null);
			resultPane_.setVisible(false);

			// resize the dialog
			dialog_.pack();
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
					closeButton_.doClick();
				}
			}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
			stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
			rootPane.registerKeyboardAction(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					findButton_.doClick();
				}
			}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
			return rootPane;
		}
	}
}