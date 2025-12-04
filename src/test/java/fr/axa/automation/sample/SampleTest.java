package fr.axa.automation.sample;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;


public class SampleTest {
	private WebDriver webDriver;

    @BeforeEach
    public void setUp() throws Exception {
        WebDriverManager.edgedriver().setup();
        webDriver =  new EdgeDriver();
    }

    @AfterEach
    public void afterScenario()  throws Exception {
        webDriver.quit();
    }

	@Test
	public void testEndToEndHomeInsurance() throws InterruptedException {
		openApplication();
        testLoginPage();
        testResearchPage();
        fillHomeDetail();
	}

    private void openApplication() {
        webDriver.get("https://axafrance.github.io/webengine-dotnet/demo/home-insurance/");
        webDriver.manage().window().maximize();
    }

    private void testLoginPage() {
        fillLogin();
        fillPassword();
        clickNextButton();
    }

    private void fillLogin() {
        WebElement loginWebElement = waitForVisibility(webDriver, By.name("login"), 30);
        loginWebElement.sendKeys("login");
    }

    private void fillPassword() {
        WebElement passwordWebElement = waitForVisibility(webDriver, By.name("password"), 30);
        passwordWebElement.sendKeys("test");
    }

    private void clickNextButton() {
        WebElement nextButton = waitForVisibility(webDriver, By.xpath("/html/body/div/div[4]/button[2]"), 30);
        nextButton.click();
    }

    private void testResearchPage() {
        // Remplir le champ prospectId
        WebElement prospectIdInput = waitForVisibility(webDriver, By.name("prospectId"), 30);
        prospectIdInput.sendKeys("12345");

        // Remplir le champ prospectName
        WebElement prospectNameInput = waitForVisibility(webDriver, By.name("prospectName"), 30);
        prospectNameInput.sendKeys("Dupont");

        // Cliquer sur le bouton Search
        WebElement searchButton = waitForVisibility(webDriver, By.xpath("//button[contains(text(),'Search')]"), 30);
        searchButton.click();

        // Cliquer sur le bouton Next Step
        WebElement nextStepButton = waitForVisibility(webDriver, By.xpath("//button[contains(text(),'Next Step')]"), 30);
        nextStepButton.click();
    }

    private void fillHomeDetail() throws InterruptedException {
        // Localisation
        waitForVisibility(webDriver, By.name("streetNumber"), 30).sendKeys("12");
        waitForVisibility(webDriver, By.name("streetName"), 30).sendKeys("Rue de la Paix");
        waitForVisibility(webDriver, By.name("city"), 30).sendKeys("Paris");
        waitForVisibility(webDriver, By.name("postcode"), 30).sendKeys("75001");
        waitForVisibility(webDriver, By.name("region"), 30).sendKeys("Île-de-France");
        WebElement countrySelect = waitForVisibility(webDriver, By.name("country"), 30);
        countrySelect.sendKeys("France");

        // Type de logement : Appartement
        WebElement apartmentRadio = waitForVisibility(webDriver, By.id("home-type-appt"), 30);
        apartmentRadio.click();

        // Détails appartement
        WebElement totalFloorsSelect = waitForVisibility(webDriver, By.name("total-floors"), 30);
        totalFloorsSelect.sendKeys("Between 4 to 7 floors");
        waitForVisibility(webDriver, By.name("my-floors"), 30).sendKeys("5");
        WebElement elevatorYes = waitForVisibility(webDriver, By.id("elevator-yes"), 30);
        elevatorYes.click();

        // Champs communs
        WebElement roomsSelect = waitForVisibility(webDriver, By.name("rooms"), 30);
        roomsSelect.sendKeys("3 Rooms");
        waitForVisibility(webDriver, By.name("surface"), 30).sendKeys("80");

        WebElement nextStepButton = waitForElementClickable(webDriver, By.xpath("//button[contains(text(),'Next Step')]"), 30);
        waitUntilElementInView(webDriver, nextStepButton, 30);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].focus();", nextStepButton);
        nextStepButton.click();
    }


    public static WebElement waitForVisibility(WebDriver driver, By locator, int timeoutInSeconds) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))  // Maximum time to wait
                .pollingEvery(Duration.ofMillis(500)) // Interval between each poll
                .ignoring(NoSuchElementException.class); // Exceptions to ignore
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForElementClickable(WebDriver driver, By locator, int timeoutInSeconds) {
        WebElement webElement = waitForVisibility( driver, locator, timeoutInSeconds);
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))  // Maximum time to wait
                .pollingEvery(Duration.ofMillis(500)) // Interval between each poll
                .ignoring(ElementClickInterceptedException.class); // Exceptions to ignore
        return wait.until(ExpectedConditions.elementToBeClickable(webElement));
    }

    public static void waitUntilEndOfPage(WebDriver driver, int timeoutInSeconds) {
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                .pollingEvery(Duration.ofMillis(500))
                .until(d -> {
                    JavascriptExecutor js = (JavascriptExecutor) d;
                    Long scrollY = (Long) js.executeScript("return window.scrollY + window.innerHeight;");
                    Long pageHeight = (Long) js.executeScript("return document.body.scrollHeight;");
                    return scrollY >= pageHeight;
                });
    }

    public static void waitUntilElementHasFocus(WebDriver driver, WebElement element, int timeoutInSeconds) {
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                .pollingEvery(Duration.ofMillis(500))
                .until(d -> {
                    JavascriptExecutor js = (JavascriptExecutor) d;
                    return (Boolean) js.executeScript("return arguments[0] === document.activeElement;", element);
                });
    }

    // Fait défiler la page jusqu’à l’élément donné
    public static void scrollToElement(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    // Attends que l’élément soit dans la fenêtre après avoir scrollé
    public static void waitUntilElementInView(WebDriver driver, WebElement element, int timeoutInSeconds) {
        scrollToElement(driver, element);
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                .pollingEvery(Duration.ofMillis(500))
                .until(d -> {
                    JavascriptExecutor js = (JavascriptExecutor) d;
                    return (Boolean) js.executeScript(
                            "const rect = arguments[0].getBoundingClientRect();" +
                                    "return rect.top >= 0 && rect.bottom <= window.innerHeight;", element);
                });
    }


}
