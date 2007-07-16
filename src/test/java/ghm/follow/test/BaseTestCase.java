package ghm.follow.test;

import ghm.follow.FileFollower;
import ghm.follow.OutputDestination;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Date;

import junit.framework.TestCase;

public abstract class BaseTestCase extends TestCase {

	public BaseTestCase(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		followedFile_ = File.createTempFile(new Date().toString(), null);
		followedFile_.deleteOnExit();
		followedFileWriter_ = new BufferedWriter(new FileWriter(followedFile_));
		follower_ = new FileFollower(followedFile_, new OutputDestination[0]);
	}

	public void tearDown() throws Exception {
		follower_.stopAndWait();
		followedFileWriter_.flush();
		followedFileWriter_.close();
	}

	protected void writeToFollowedFileAndWait(String string) throws Exception {
		followedFileWriter_.write(string);
		followedFileWriter_.flush();
		Thread.sleep(follower_.getLatency() + 100);
	}
	
	protected FileFollower follower_;
	protected File followedFile_;
	protected Writer followedFileWriter_;
}