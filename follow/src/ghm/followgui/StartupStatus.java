// Copyright (C) 2000 Greg Merrill (greghmerrill@yahoo.com)
// Distributed under the terms of the GNU General Public License (version 2)
// For details on the GNU GPL, please visit http://www.gnu.org/copyleft/gpl.html
// To find out more about this and other free software by Greg Merrill, 
//  please visit http://gregmerrill.imagineis.com

package ghm.followgui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
Window which displays a progress bar during startup.
@author <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
*/
class StartupStatus extends JWindow {
  
  StartupStatus (ResourceBundle resourceBundle) {
    LOAD_SYSTEM_FONTS = new Task(
      2, resourceBundle.getString("startupStatus.loadSystemFonts")
    );
    allTasks_.add(LOAD_SYSTEM_FONTS);

    CREATE_WIDGETS = new Task(
      2, resourceBundle.getString("startupStatus.createWidgets")
    );
    allTasks_.add(CREATE_WIDGETS);

    int taskWeightSummation = 0;
    for (int i=0; i < allTasks_.size(); i++) {
      taskWeightSummation += ((Task)allTasks_.get(i)).weight_;
    }
    progressBar_ = new JProgressBar(0, taskWeightSummation);    
    progressBar_.setStringPainted(true);
    progressBar_.setString(((Task)allTasks_.get(0)).inProgressMessage_);

    BorderLayout borderLayout = new BorderLayout();
    borderLayout.setVgap(6);
    JPanel panel = new JPanel(borderLayout);
    panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
    JLabel label = new JLabel(resourceBundle.getString("startupStatus.label"));
    label.setHorizontalAlignment(JLabel.CENTER);
    panel.add(label, BorderLayout.NORTH);
    panel.add(progressBar_, BorderLayout.SOUTH);
    this.getContentPane().add(panel);
  }
  private int currentTask_;
  
  void markDone (final Task task) {
    if (allTasks_.indexOf(task) != currentTask_) { throw new RuntimeException(
      "Programmatic error: tasks should be marked done sequentially"
    );}
    progressBar_.setValue(progressBar_.getValue() + task.weight_);
    currentTask_++;
    if (currentTask_ < allTasks_.size()) { progressBar_.setString(
      ((Task)allTasks_.get(currentTask_)).inProgressMessage_
    );}
  }
  
  private JProgressBar progressBar_ = new JProgressBar();

  // Must be final to force clients to use the Tasks declared 'final' when 
  // marking Tasks as done
  private final List allTasks_ = new ArrayList();
  
  // Complete set of Tasks which need to be completed to start the Follow app
  final Task LOAD_SYSTEM_FONTS;
  final Task CREATE_WIDGETS;

  /** Instances of this class represent significant tasks which must be 
    accomplished in order to start the Follow application. */
  static class Task { 
    // private to prevent instantiation by clients
    private Task (int weight, String inProgressMessage) { 
      weight_ = weight; 
      inProgressMessage_ = inProgressMessage;
    }

    // final to prevent modification by clients
    final private int weight_;
    final private String inProgressMessage_;
  }
  
}

