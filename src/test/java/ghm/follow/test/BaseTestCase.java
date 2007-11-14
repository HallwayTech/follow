package ghm.follow.test;

import ghm.follow.FileFollower;
import ghm.follow.io.OutputDestination;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.After;
import org.junit.Before;

public abstract class BaseTestCase {
	protected FileFollower follower_;
	protected File followedFile_;
	protected Writer followedFileWriter_;

	@Before
	public void setUp() throws Exception {
		followedFile_ = createTempFile();
		followedFile_.deleteOnExit();
		followedFileWriter_ = new BufferedWriter(new FileWriter(followedFile_));
		follower_ = new FileFollower(followedFile_, new OutputDestination[0]);
	}

	@After
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

	protected void clearFollowedFile() throws Exception {
		followedFileWriter_.close();
		new FileWriter(followedFile_, false).write("");
	}

	protected File createTempFile() throws IOException {
		File file = File.createTempFile("followedFile", null);
		file.deleteOnExit();
		return file;
	}
}