package ghm.followgui;

import java.io.File;

public class TestSystemInterface implements SystemInterface {

  public File getFileFromUser () { return fileFromUser_; }

  public void setFileFromUser (File file) { fileFromUser_ = file; }

  public void exit (int code) { exitCalled_ = true; }

  public boolean exitCalled () { return exitCalled_; }

  public void reset () {
    exitCalled_ = false;
  }

  private File fileFromUser_;
  private boolean exitCalled_;

}

