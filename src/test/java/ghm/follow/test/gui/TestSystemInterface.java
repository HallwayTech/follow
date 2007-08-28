package ghm.follow.test.gui;

import ghm.follow.systemInterface.SystemInterface;

import java.io.File;

public class TestSystemInterface implements SystemInterface {
	private File fileFromUser_;
	private boolean exitCalled_;

	public File getFileFromUser() {
		return fileFromUser_;
	}

	public void setFileFromUser(File file) {
		fileFromUser_ = file;
	}

	public void exit(int code) {
		exitCalled_ = true;
	}

	public boolean exitCalled() {
		return exitCalled_;
	}

	public void reset() {
		exitCalled_ = false;
	}
}