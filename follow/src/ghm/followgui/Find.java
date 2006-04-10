package ghm.followgui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class Find extends FollowAppAction
{
  FindDialog dialog_;
  JTextField find_;

  Find (FollowApp app) {
    super(
      app, 
      app.resBundle_.getString("action.Find.name"),
      app.resBundle_.getString("action.Find.mnemonic"),
      app.resBundle_.getString("action.Find.accelerator")
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
    find_.grabFocus();
    find_.selectAll();
    dialog_.show();
    app_.setCursor(Cursor.DEFAULT_CURSOR);
  }

  class FindDialog extends JDialog {
    FindDialog() {
      super(
          Find.this.app_.frame_, 
          Find.this.app_.resBundle_.getString("dialog.Find.title"), 
          false
      );
      addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e)
        {
          if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            hide();
          }
        }
      });
      setResizable(false);
      JComponent contentPane = (JComponent)getContentPane();
      contentPane.setBorder(
          BorderFactory.createEmptyBorder(2, 2, 2, 2)
      );

      JPanel findPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

      // add the find field & label
      findPanel.add(new JLabel(app_.resBundle_.getString("dialog.Find.findText.label")));
      find_ = new JTextField(15);
      find_.setHorizontalAlignment(JTextField.LEFT);
//      find_.addKeyListener(new KeyAdapter() {
//        public void keyPressed(KeyEvent e)
//        {
//          if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//            doFind();
//          }
//        }
//      });
      findPanel.add(find_);
      // add the find button
      JButton btnFind = new JButton(app_.resBundle_.getString("dialog.Find.findButton.label"));
      btnFind.setMnemonic(app_.resBundle_.getString("dialog.Find.findButton.mnemonic").charAt(0));
      btnFind.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e)
        {
          doFind();
        }
      });
      // add the close button
      JButton btnClose = new JButton(app_.resBundle_.getString("dialog.Find.closeButton.label"));
      btnClose.setMnemonic(app_.resBundle_.getString("dialog.Find.closeButton.mnemonic").charAt(0));
      btnClose.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e)
        {
          dialog_.hide();
        }
      });
      // add the buttons to the dialog
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      buttonPanel.add(btnFind);
      buttonPanel.add(btnClose);
      // add everything to the content pane
      contentPane.add(findPanel, BorderLayout.CENTER);
      contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Override method to add ESCAPE key action for window close
     */
    protected JRootPane createRootPane()
    {
      KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      JRootPane rootPane = new JRootPane();
      rootPane.registerKeyboardAction(new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
          hide();
       }
      }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
      stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
      rootPane.registerKeyboardAction(new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
          doFind();
       }
      }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
      return rootPane;
    }
  }

  private void doFind() {
    // get the current selected tab
    FileFollowingPane pane = (FileFollowingPane) app_.tabbedPane_.getSelectedComponent();
    // search the tab with the given text
    SearchableTextArea textArea = (SearchableTextArea) pane.getTextArea();
    textArea.highlight(find_.getText());
  }
}