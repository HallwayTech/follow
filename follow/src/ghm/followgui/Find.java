package ghm.followgui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Find extends FollowAppAction
{
  FindDialog dialog_;
  JTextField find_;

  Find (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.Find.name"),
      app.resBundle_.getString("action.Find.mnemonic"),
      app.resBundle_.getString("action.Find.accelerator"),
      app.resBundle_.getString("action.Find.icon")
    );    
  }

  public void actionPerformed (ActionEvent e) {
    app_.setCursor(Cursor.WAIT_CURSOR);
    if (dialog_ == null) {
      dialog_ = new FindDialog();
      dialog_.setLocationRelativeTo(app_.frame_);
      dialog_.setLocation(30, 30);
      dialog_.pack();
    }
    dialog_.show();
    app_.setCursor(Cursor.DEFAULT_CURSOR);
  }
  
  class FindDialog extends JDialog {
    FindDialog() {
      super(
          Find.this.app_.frame_, 
          Find.this.app_.resBundle_.getString("dialog.Find.title"), 
          true
      );
      JComponent contentPane = (JComponent)getContentPane();
      contentPane.setBorder(
          BorderFactory.createEmptyBorder(2, 2, 2, 2)
      );
    
      JPanel findPanel = new JPanel(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.WEST;
      gbc.ipadx = 4;
      
      // add the find field & label
      gbc.gridy = 0;
      findPanel.add(new JLabel(app_.resBundle_.getString("dialog.Find.findText.label")),
          gbc);
      find_ = new JTextField();
      find_.setHorizontalAlignment(JTextField.LEFT);
      gbc.gridx = 1;      
      gbc.weightx = 1;
      gbc.ipadx = 0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      findPanel.add(find_, gbc);
      // add the find button
      JButton btnFind = new JButton(app_.resBundle_.getString("dialog.Find.findButton.label"));
      btnFind.setMnemonic(app_.resBundle_.getString("dialog.Find.findButton.mnemonic").charAt(0));
      btnFind.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e)
        {
          // get the current selected tab
          FileFollowingPane pane = (FileFollowingPane) app_.tabbedPane_.getSelectedComponent();
          // search the tab with the given text
          SearchableTextArea textArea = (SearchableTextArea) pane.getTextArea();
          int findPos = textArea.search(find_.getText());
          System.out.println("Found it here: " + findPos);
        }
      });
      JButton btnCancel = new JButton(app_.resBundle_.getString("dialog.Find.cancelButton.label"));
      btnCancel.setMnemonic(app_.resBundle_.getString("dialog.Find.cancelButton.mnemonic").charAt(0));
      btnCancel.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e)
        {
          dialog_.hide();
        }
      });

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      buttonPanel.add(btnFind);
      buttonPanel.add(btnCancel);

      contentPane.add(findPanel, BorderLayout.CENTER);
      contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }
  }
}