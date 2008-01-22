package ghm.follow.test;

import ghm.follow.FileFollower;
import ghm.follow.io.OutputDestination;
import ghm.follow.io.PrintStreamDestination;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrintStreamDestinationTest extends BaseTestCase
{

	@Test
	public void testPrintCalled() throws Exception
	{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		PrintStreamDestination dest = new PrintStreamDestination(new PrintStream(byteStream));
		follower_ = new FileFollower(followedFile_, new OutputDestination[] { dest });
		follower_.start();
		String control = "control";
		writeToFollowedFileAndWait(control);
		assertEquals(control, new String(byteStream.toByteArray()));
	}
}