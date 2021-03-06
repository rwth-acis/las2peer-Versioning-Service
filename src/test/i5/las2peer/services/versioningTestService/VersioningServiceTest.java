package i5.las2peer.services.versioningTestService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import i5.las2peer.p2p.LocalNode;
import i5.las2peer.security.ServiceAgent;
import i5.las2peer.security.UserAgent;
import i5.las2peer.services.versioningService.VersioningService;
import i5.las2peer.testing.MockAgentFactory;
import i5.las2peer.webConnector.WebConnector;
import i5.las2peer.webConnector.client.ClientResponse;
import i5.las2peer.webConnector.client.MiniClient;

/**
 * Example Test Class demonstrating a basic JUnit test structure.
 *
 */
public class VersioningServiceTest {

	private static final String HTTP_ADDRESS = "http://127.0.0.1";
	private static final int HTTP_PORT = WebConnector.DEFAULT_HTTP_PORT;

	private static LocalNode node;
	private static WebConnector connector;
	private static ByteArrayOutputStream logStream;

	private static UserAgent testAgent;
	private static final String testPass = "adamspass";

	private static final String mainPath = "template/";

	/**
	 * Called before the tests start.
	 * 
	 * Sets up the node and initializes connector and users that can be used throughout the tests.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void startServer() throws Exception {

		// start node
		node = LocalNode.newNode();
		testAgent = MockAgentFactory.getAdam();
		testAgent.unlockPrivateKey(testPass); // agent must be unlocked in order to be stored
		node.storeAgent(testAgent);
		node.launch();

		// during testing, the specified service version does not matter
		ServiceAgent testService = ServiceAgent.createServiceAgent(VersioningService.class.getName(), "a pass");
		testService.unlockPrivateKey("a pass");

		node.registerReceiver(testService);

		// start connector
		logStream = new ByteArrayOutputStream();

		connector = new WebConnector(true, HTTP_PORT, false, 1000);
		connector.setLogStream(new PrintStream(logStream));
		connector.start(node);
		Thread.sleep(1000); // wait a second for the connector to become ready
		testAgent = MockAgentFactory.getAdam(); // get a locked agent

	
		connector.updateServiceList();
		// avoid timing errors: wait for the repository manager to get all services before continuing
		try {
			System.out.println("waiting..");
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Called after the tests have finished. Shuts down the server and prints out the connector log file for reference.
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void shutDownServer() throws Exception {

		connector.stop();
		node.shutDown();

		connector = null;
		node = null;

		LocalNode.reset();

		System.out.println("Connector-Log:");
		System.out.println("--------------");

		System.out.println(logStream.toString());

	}

	/**
	 * 
	 * Tests the validation method.
	 * 
	 */
	@Test 
	public void testSimpleGet() {
		MiniClient c = new MiniClient();
		c.setAddressPort(HTTP_ADDRESS, HTTP_PORT);

		try {
			c.setLogin(Long.toString(testAgent.getId()), testPass);
			ClientResponse result = c.sendRequest("GET", mainPath + "testsimpleget", "");
			//assertEquals(200, result.getHttpCode());
			//assertTrue(result.getResponse().trim().contains("success")); // YOUR RESULT VALUE HERE
			//System.out.println("Result of 'testSimpleGet': " + result.getResponse().trim());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception: " + e);
		}

	}
	
	/*@Test 
	public void testCallback() {
		MiniClient c = new MiniClient();
		c.setAddressPort(HTTP_ADDRESS, HTTP_PORT);

		try {
			c.setLogin(Long.toString(testAgent.getId()), testPass);
			ClientResponse result = c.sendRequest("GET", mainPath + "testcallback", "");
			assertTrue(result.getResponse().trim().contains("code")); // YOUR RESULT VALUE HERE
			System.out.println("Result of 'testcallback': " + result.getResponse().trim());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception: " + e);
		}

	}*/
	
	


	/**
	 * 
	 * Test the example method that consumes one path parameter which we give the value "testInput" in this test.
	 * 
	 */
//	@Test
//	public void testPost() {
//		MiniClient c = new MiniClient();
//		c.setAddressPort(HTTP_ADDRESS, HTTP_PORT);
//
//		try {
//			c.setLogin(Long.toString(testAgent.getId()), testPass);
//			ClientResponse result = c.sendRequest("POST", mainPath + "post/testInput", ""); // testInput is
//																							// the pathParam
//			assertEquals(200, result.getHttpCode());
//			assertTrue(result.getResponse().trim().contains("testInput")); // "testInput" name is part of response
//			System.out.println("Result of 'testPost': " + result.getResponse().trim());
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail("Exception: " + e);
//		}
//	}

	/**
	 * Test the TemplateService for valid rest mapping. Important for development.
	 */
	@Test
	public void testDebugMapping() {
		VersioningService cl = new VersioningService();
		assertTrue(cl.debugMapping());
	}

}
