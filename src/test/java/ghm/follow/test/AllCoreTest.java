package ghm.follow.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FileFollowerT.class, JTextComponentDestinationT.class,
		PrintStreamDestinationT.class })
public class AllCoreTest {
}
