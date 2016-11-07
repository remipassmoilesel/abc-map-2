package abcmap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import abcmap.utils.threads.ThreadAccessControl;

public class ThreadManagerTests {

	@BeforeClass
	public static void methodBeforeAllTests() {
		Initialisation.doInit(null);
	}

	/**
	 * Test des méthode de réglementation d'accés aux threads
	 */
	@Test
	public void threadAccessTest() {

		// parametres par defaut: 1 seul acces possible
		Assert.assertTrue(ThreadAccessControl.get(0).askAccess());

		Assert.assertFalse(ThreadAccessControl.get(0).askAccess());

		// deuxieme tentative avec un gestionnaire different
		Assert.assertTrue(ThreadAccessControl.get(1).askAccess());

		Assert.assertFalse(ThreadAccessControl.get(1).askAccess());

	}

}
