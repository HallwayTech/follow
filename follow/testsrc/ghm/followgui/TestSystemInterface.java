package ghm.followgui;

import java.io.File;

public class TestSystemInterface implements SystemInterface {

  public File getFileFromUser () { return fileFromUser_; }

  public void setFileFromUser (File file) { fileFromUser_ = file; }

  private File fileFromUser_;
}

