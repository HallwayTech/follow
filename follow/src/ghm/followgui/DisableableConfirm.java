// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class DisableableConfirm extends JDialog {
  
  DisableableConfirm (
    Frame parent,
    String title,
    String message,
    String confirmButtonText,
    String doNotConfirmButtonText,
    String disableText
  ) {
    super(parent, title, true);

    // messagePanel will contain the string contents of the message
    JPanel messagePanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    StringTokenizer stknzr = new StringTokenizer(message, "\n\r");
    gbc.gridy=0;
    while (stknzr.hasMoreTokens()) {
      messagePanel.add(new JLabel(stknzr.nextToken()), gbc);
      gbc.gridy++;
    }

    // controlPanel will contain the confirm/doNotConfirm buttons and the
    // disable checkbox
    JPanel controlPanel = new JPanel(new GridBagLayout());
    gbc = new GridBagConstraints();

    JPanel buttonPanel = new JPanel();
    JButton confirmButton = new JButton(confirmButtonText);
    confirmButton.addActionListener(new ActionListener () {
      public void actionPerformed (ActionEvent e) {
        confirmed_ = true;
        DisableableConfirm.this.dispose();
      }
    });
    buttonPanel.add(confirmButton);
    JButton doNotConfirmButton = new JButton(doNotConfirmButtonText);
    doNotConfirmButton.addActionListener(new ActionListener () {
      public void actionPerformed (ActionEvent e) {
        confirmed_ = false;
        DisableableConfirm.this.dispose();
      }
    });
    buttonPanel.add(doNotConfirmButton);    
    controlPanel.add(buttonPanel, gbc);

    disabledCheckBox_ = new JCheckBox(disableText);
    gbc.gridy = 1;
    controlPanel.add(disabledCheckBox_, gbc);

    JPanel contentPane = new JPanel(new BorderLayout(0, 10));
    contentPane.setBorder(
      BorderFactory.createEmptyBorder(12, 12, 11, 11)
    );
    contentPane.add(messagePanel, BorderLayout.CENTER);
    contentPane.add(controlPanel, BorderLayout.SOUTH);
    this.setContentPane(contentPane);
    FollowApp.centerWindowInScreen(this);
  }
  private JCheckBox disabledCheckBox_;
  private boolean confirmed_;
  
  boolean markedDisabled () {
    return disabledCheckBox_.isSelected();
  }
  
  boolean markedConfirmed () {
    return confirmed_;
  }
  
}

