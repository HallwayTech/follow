package ghm.follow.test.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class CommandLineT extends AppLaunchingTestCase {
    @Override
    @Before
    public void setUp() throws Exception {
	super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {
	super.tearDown();
	AppLaunchingTestCase.shutdown();
    }

    @AfterClass
    public static void afterClass() {
	// AppLaunchingTestCase.afterClass();
    }

    @Test
    public void testNoArgs() throws Exception {
	AppLaunchingTestCase.launch(null);
	// should be no file as we're opening a new instance (no history) with
	// no arguments (no requested files)
	assertEquals(0, app.getAttributes().getFollowedFiles().size());
    }

    @Test
    public void testOneArg() throws Exception {
	File temp = createTempFile();
	String[] args = new String[] { temp.toString() };
	AppLaunchingTestCase.launch(args);
	List<File> followedFiles = app.getAttributes().getFollowedFiles();
	assertEquals(1, followedFiles.size());
	File followedFile = followedFiles.get(0);
	assertEquals("File found doesn't match expected file", temp,
		followedFile);
    }

    @Test
    public void testOneArgDuplicate() throws Exception {
	File temp = createTempFile();
	String[] args = new String[] { temp.toString() };
	AppLaunchingTestCase.launch(args);
	List<File> followedFiles = app.getAttributes().getFollowedFiles();
	assertTrue("Expecting 1 file to be followed.  Found "
		+ followedFiles.size(), followedFiles.size() == 1);
	File followedFile = followedFiles.get(0);
	assertEquals(temp, followedFile);
	AppLaunchingTestCase.shutdown();

	// reopen app with same file as argument
	AppLaunchingTestCase.launch(args);
	followedFiles = app.getAttributes().getFollowedFiles();
	// should still be one because Follow shouldn't open the same file twice
	assertEquals(1, followedFiles.size());
	// make sure followedFile is the expected file
	followedFile = followedFiles.get(0);
	assertEquals(temp, followedFile);
    }

    @Test
    public void testOneArgReopen() throws Exception {
	File temp = createTempFile();
	String[] args = new String[] { temp.toString() };
	AppLaunchingTestCase.launch(args);
	List<File> followedFiles = app.getAttributes().getFollowedFiles();
	assertTrue("Expecting 1 file to be followed.  Found "
		+ followedFiles.size(), followedFiles.size() == 1);
	File followedFile = followedFiles.get(0);
	assertEquals(temp, followedFile);
	AppLaunchingTestCase.shutdown();

	// reopen app with same file as argument
	AppLaunchingTestCase.launch(args);
	followedFiles = app.getAttributes().getFollowedFiles();
	// should still be one because Follow shouldn't open the same file twice
	assertEquals(1, followedFiles.size());
	// make sure followedFile is the expected file
	followedFile = followedFiles.get(0);
	assertEquals(temp, followedFile);
    }

    @Test
    public void testTwoArgs() throws Exception {
	File[] temp = new File[2];
	temp[0] = createTempFile();
	temp[1] = createTempFile();
	String[] args = new String[] { temp[0].toString(), temp[1].toString() };
	AppLaunchingTestCase.launch(args);
	List<File> followedFiles = app.getAttributes().getFollowedFiles();
	assertEquals(true, followedFiles.size() == 2);
	assertEquals(temp[0], followedFiles.get(0));
	assertEquals(temp[1], followedFiles.get(1));
    }

    @Test
    public void testDuplicateArgs() throws Exception {
	File temp = createTempFile();
	String[] args = new String[] { temp.toString(),
		createTempFile().toString(), temp.toString() };
	AppLaunchingTestCase.launch(args);
	List<File> followedFiles = app.getAttributes().getFollowedFiles();
	assertEquals(temp, followedFiles.get(0));
	assertEquals(true, followedFiles.size() == 2);
    }
}