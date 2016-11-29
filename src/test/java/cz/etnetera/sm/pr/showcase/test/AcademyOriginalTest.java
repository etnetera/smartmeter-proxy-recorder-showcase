package cz.etnetera.sm.pr.showcase.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

@Ignore
public class AcademyOriginalTest {

	private String baseUrl;

	private WebDriver driver;

	@Before
	public void setUp() throws Exception {
		baseUrl = "http://academy.smartmeter.io/university";
		driver = new FirefoxDriver();
	}

	@Test
	public void test() throws Exception {
		driver.get(baseUrl);
		driver.findElement(By.cssSelector("#toc a[href$=assignment-page]")).click();
		driver.findElement(By.cssSelector("#toc a[href$=submit-page]")).click();

		WebElement formEl = driver.findElement(By.cssSelector("form.university-form"));
		formEl.findElement(By.name("name")).sendKeys("Jan Verner");
		formEl.findElement(By.name("description")).sendKeys("Hello SmartMeter.io!");
		formEl.findElement(By.cssSelector("button[type=submit]")).click();
	}

	@After
	public void tearDown() throws Exception {
		if (driver != null) driver.quit();
	}

}
