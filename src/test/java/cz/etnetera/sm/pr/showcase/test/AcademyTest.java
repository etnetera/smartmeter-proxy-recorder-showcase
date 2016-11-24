package cz.etnetera.sm.pr.showcase.test;

import cz.etnetera.sm.pr.SMProxyRecorderApi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class AcademyTest {

	private static final boolean USE_SM_RECORDER = true;

	private static final String SM_RECORDER_ENDPOINT = "http://localhost:8085";

	private static final String SM_RECORDING_PROXY = "localhost:8080";

	private static final String SM_TEST_NAME = "academy-from-selenium";

	private static final boolean RUN_SM_TEST = false;

	private static final long SM_PAUSE_DURATION_MILLIS = 5000;

	private String baseUrl;

	private WebDriver driver;

	private SMProxyRecorderApi smRecorderApi;

	@Before
	public void setUp() throws Exception {
		baseUrl = "http://academy.smartmeter.io/university";

		if (USE_SM_RECORDER) {
			smRecorderApi = new SMProxyRecorderApi(SM_RECORDER_ENDPOINT).status();

			DesiredCapabilities caps = new DesiredCapabilities();
			Proxy proxy = new Proxy();
			proxy.setProxyType(Proxy.ProxyType.MANUAL);
			String recordingUrl = SM_RECORDING_PROXY;
			proxy.setHttpProxy(recordingUrl);
			proxy.setSslProxy(recordingUrl);
			proxy.setFtpProxy(recordingUrl);

			caps.setCapability(CapabilityType.PROXY, proxy);
			caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

			driver = new FirefoxDriver(caps);
		} else {
			driver = new FirefoxDriver();
		}
	}

	@Test
	public void test() throws Exception {
		if (USE_SM_RECORDER) smRecorderApi.startSubtest("University", 1, 300, 0);

		driver.get(baseUrl);
		insertSmPause();

		driver.findElement(By.cssSelector("#toc a[href$=assignment-page]")).click();
		insertSmPause();

		driver.findElement(By.cssSelector("#toc a[href$=submit-page]")).click();
		insertSmPause();

		if (USE_SM_RECORDER) {
			smRecorderApi.activateCSV("academy-data.csv", null, null);
			smRecorderApi.addReplacers("name", "description", "secretValue");
		}
		WebElement formEl = driver.findElement(By.cssSelector("form.university-form"));
		formEl.findElement(By.name("name")).sendKeys("Jan Verner");
		formEl.findElement(By.name("description")).sendKeys("Hello SmartMeter.io!");
		formEl.findElement(By.cssSelector("button[type=submit]")).click();
		insertSmPause();

		if (USE_SM_RECORDER) {
			smRecorderApi.finishSubtest();
			smRecorderApi.exportTest(SM_TEST_NAME);
			smRecorderApi.clearRecording();
			if (RUN_SM_TEST) smRecorderApi.runTest(SM_TEST_NAME, null, null);
		}
	}

	@After
	public void tearDown() throws Exception {
		if (driver != null) driver.quit();
		if (smRecorderApi != null) smRecorderApi.shutdown();
	}

	private void pause(long duration) throws InterruptedException {
		Thread.sleep(duration);
	}

	private void insertSmPause() throws InterruptedException, SMProxyRecorderApi.SMProxyRecorderException {
		if (USE_SM_RECORDER) {
			pause(SM_PAUSE_DURATION_MILLIS);
			smRecorderApi.insertPause(SM_PAUSE_DURATION_MILLIS);
		}
	}

}
