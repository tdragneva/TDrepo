package object;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class RegistrationPage {
    public static final String REGISTRATION_URL = "http://training.skillo-bg.com:4200/users/register";
    private final WebDriver webDriver;


    public RegistrationPage(WebDriver driver)  {

        this.webDriver = driver;
    }

    public boolean isUrlLoaded(){
        WebDriverWait wait = new WebDriverWait(this.webDriver, Duration.ofSeconds(15));
        return wait.until(ExpectedConditions.urlToBe(REGISTRATION_URL));

    }

    public void navigateTo() {
        this.webDriver.get(REGISTRATION_URL);
    }

}
