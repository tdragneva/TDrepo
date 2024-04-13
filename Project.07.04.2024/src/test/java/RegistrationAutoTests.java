import factory.FactoryRegistrationPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class RegistrationAutoTests {
    public static final String TEST_RESOURCES_DIR = "src\\test\\resources\\";
    public static final String DOWNLOAD_DIR = TEST_RESOURCES_DIR.concat("download\\");
    public static final String SCREENSHOTS_DIR = TEST_RESOURCES_DIR.concat("screenshots\\");
    public static final String REPORTS_DIR = TEST_RESOURCES_DIR.concat("reports\\");
    private WebDriver webDriver;
    FactoryRegistrationPage registerPage;


    @BeforeClass(alwaysRun = true)
    protected final void setupTestSuite() throws  IOException{
        cleanDirectory(SCREENSHOTS_DIR);
        cleanDirectory(REPORTS_DIR);

    }

    @BeforeMethod(alwaysRun = true)
    protected final void setUpTest(){
        WebDriverManager.chromedriver().setup();
        this.webDriver = new ChromeDriver(configChromeOptions());
        this.webDriver.manage().window().maximize();
        this.webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));
        this.webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        registerPage = new FactoryRegistrationPage(webDriver);

    }


    @AfterMethod(alwaysRun = true)
    protected final void tearDownTest(ITestResult testResult){
        takeScreenshot(testResult);
        quitDriver();
    }
    @AfterClass(alwaysRun = true)
    public void deleteDownloadFiles() throws IOException{
        cleanDirectory(DOWNLOAD_DIR);
    }

    private void quitDriver() {
        if (this.webDriver != null){
            this.webDriver.quit();
        }
    }

    public static String generateRandomUsername() {
        String username = UUID.randomUUID().toString().replaceAll("-", "");
        return username.substring(0, 6);
    }

    public static String generateRandomEmail() {
        String newEmailAddres = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
        String domain = "te.st";
        return newEmailAddres + "@" + domain;
    }

    public static String generateRandomPassword() {
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = upperCaseLetters.toLowerCase();
        String numbers = "0123456789";
        String allCharacters = upperCaseLetters + lowerCaseLetters + numbers;
        StringBuilder password = new StringBuilder();

        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(allCharacters.length());
            password.append(allCharacters.charAt(randomIndex));
        }
        return password.toString();
    }

    private ChromeOptions configChromeOptions(){

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory",
                System.getProperty("user.dir").concat("\\").concat(DOWNLOAD_DIR));

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("prefs", prefs);
        chromeOptions.addArguments("disable-popup-blocking");

        return chromeOptions;
    }

    private void cleanDirectory(String directoryPath) throws IOException{
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        Assert.assertTrue(directory.isDirectory(), "Invalid directory!");

        FileUtils.cleanDirectory(directory);
        String[] fileList = directory.list();
        if (fileList != null && fileList.length == 0){
            System.out.printf("All file are deleted in Directory: %s%n", directoryPath);
        }else {
            System.out.printf("Unable to delete the files in Directory: %s%n", directoryPath);
        }
    }

    private void takeScreenshot(ITestResult testResult){
        if(ITestResult.FAILURE == testResult.getStatus()){
            try{
                TakesScreenshot takesScreenshot = (TakesScreenshot) webDriver;
                File screenshots = takesScreenshot.getScreenshotAs(OutputType.FILE);
                String testName = testResult.getName();
                FileUtils.copyFile(screenshots, new File(SCREENSHOTS_DIR.concat(testName).concat(".jpg")));
            }catch (IOException e){
                System.out.println("Unable to create a screenshot file: " + e.getMessage());
            }
        }
    }



    @Test
    public void registerLinkNavigation(){

        webDriver.get("http://training.skillo-bg.com:4200/users/login");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.urlToBe("http://training.skillo-bg.com:4200/users/login"));

        WebElement login = webDriver.findElement(new By.ByXPath("//*[@id='nav-link-login']"));
        login.click();

        WebElement registerLink = webDriver.findElement(new By.ByXPath("/html/body/app-root/div[2]/app-login/div/div/form/p[2]/a"));
        registerLink.click();
        String registrationPageLink = "http://training.skillo-bg.com:4200/users/register";

        Assert.assertTrue(registerPage.isUrlLoaded(), "Registration page not loaded");
        Assert.assertEquals(webDriver.getCurrentUrl(), registrationPageLink,"The current URL does not match the expected registration page link.");
        webDriver.close();
    }

    @Test
    public void registrationElementFieldsEnabled(){

        registerPage.navigateTo();
        Assert.assertTrue(registerPage.isUrlLoaded(), "Registration page not loaded");

        WebElement usernameField = webDriver.findElement(new By.ByXPath("/html/body/app-root/div[2]/app-register/div/div/form/div[1]/input"));
        WebElement emailField = webDriver.findElement(new By.ByXPath("/html/body/app-root/div[2]/app-register/div/div/form/div[2]/input"));
        WebElement passwordField = webDriver.findElement(new By.ById("defaultRegisterFormPassword"));
        WebElement confirmPasswordField = webDriver.findElement(new By.ById("defaultRegisterPhonePassword"));

        Assert.assertTrue(usernameField.isEnabled(), "The username field is not enabled.");
        Assert.assertTrue(emailField.isEnabled(), "The email field is not enabled.");
        Assert.assertTrue(passwordField.isEnabled(), "The first password field is not enabled.");
        Assert.assertTrue(confirmPasswordField.isEnabled(), "The confirm password field is not enabled.");

        webDriver.close();
    }


    @Test
    public void usernameEmailFieldsDataSubmission(){

        registerPage.navigateTo();
        Assert.assertTrue(registerPage.isUrlLoaded(), "Registration page not loaded");

        String username = generateRandomUsername();
        String email = generateRandomEmail();

        WebElement usernameField = webDriver.findElement(new By.ByXPath("/html/body/app-root/div[2]/app-register/div/div/form/div[1]/input"));
        usernameField.sendKeys(username);
        String enteredUsername = usernameField.getAttribute("value");

        WebElement emailField = webDriver.findElement(new By.ByXPath("/html/body/app-root/div[2]/app-register/div/div/form/div[2]/input"));
        emailField.sendKeys(email);
        String enteredEmail = emailField.getAttribute("value");

        Assert.assertEquals(enteredUsername, username, "The entered username does not match the new username.");
        Assert.assertEquals(enteredEmail, email, "The entered email does not match the new email.");

        webDriver.close();
    }



    @Test
    public void passwordsMatchValidation(){

        registerPage.navigateTo();
        Assert.assertTrue(registerPage.isUrlLoaded(), "Registration page not loaded");

        String password = generateRandomPassword();

        WebElement passwordField = webDriver.findElement(new By.ById("defaultRegisterFormPassword"));
        passwordField.sendKeys(password);
        String enteredPassword = passwordField.getAttribute("value");

        WebElement confirmPasswordField = webDriver.findElement(new By.ById("defaultRegisterPhonePassword"));
        confirmPasswordField.sendKeys(password);
        String confirmedPassword = confirmPasswordField.getAttribute("value");


        Assert.assertEquals(enteredPassword, confirmedPassword, "The entered password in the first field does not match the password from the second.");

        webDriver.close();
    }


    @Test
    public void isSignInButtonEnabled(){

        registerPage.navigateTo();
        Assert.assertTrue(registerPage.isUrlLoaded(), "Registration page not loaded");

        String username = generateRandomUsername();
        String email = generateRandomEmail();
        String password = generateRandomPassword();


        WebElement usernameField = webDriver.findElement(new By.ByXPath("/html/body/app-root/div[2]/app-register/div/div/form/div[1]/input"));
        usernameField.sendKeys(username);

        WebElement emailField = webDriver.findElement(new By.ByXPath("/html/body/app-root/div[2]/app-register/div/div/form/div[2]/input"));
        emailField.sendKeys(email);

        WebElement passwordField = webDriver.findElement(new By.ById("defaultRegisterFormPassword"));
        passwordField.sendKeys(password);

        WebElement confirmPasswordField = webDriver.findElement(new By.ById("defaultRegisterPhonePassword"));
        confirmPasswordField.sendKeys(password);

        WebElement singInButton = webDriver.findElement(new By.ById("sign-in-button"));

        Assert.assertTrue(singInButton.isEnabled(), "The Sign in button is not enabled.");

        webDriver.close();
    }

    @Test
    public void redirectionAfterRegistration(){

        registerPage.navigateTo();
        Assert.assertTrue(registerPage.isUrlLoaded(), "Registration page not loaded");

        String username = generateRandomUsername();
        String email = generateRandomEmail();
        String password = generateRandomPassword();

        WebElement usernameField = webDriver.findElement(new By.ByXPath("/html/body/app-root/div[2]/app-register/div/div/form/div[1]/input"));
        usernameField.sendKeys(username);

        WebElement emailField = webDriver.findElement(new By.ByXPath("/html/body/app-root/div[2]/app-register/div/div/form/div[2]/input"));
        emailField.sendKeys(email);

        WebElement passwordField = webDriver.findElement(new By.ById("defaultRegisterFormPassword"));
        passwordField.sendKeys(password);

        WebElement confirmPasswordField = webDriver.findElement(new By.ById("defaultRegisterPhonePassword"));
        confirmPasswordField.sendKeys(password);

        WebElement singInButton = webDriver.findElement(new By.ById("sign-in-button"));
        singInButton.click();

        String registeredUserUrl = "http://training.skillo-bg.com:4200/posts/all";

        Duration timeoutDuration = Duration.ofSeconds(20);

        WebDriverWait wait = new WebDriverWait(webDriver, timeoutDuration);
        wait.until(ExpectedConditions.urlToBe(registeredUserUrl));


        Assert.assertEquals(webDriver.getCurrentUrl(), registeredUserUrl, "The current ULR does not match the registration URL.");

        webDriver.close();
    }

}
