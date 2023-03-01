package watcherbot.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
@ComponentScan("watcherbot.parser")
@PropertySource("application.properties")
public class PageParserTestConfig {
    @Bean
    @Profile("!remotechrome")
    public WebDriver getWebDriver() {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver();
    }

    @Bean
    @Profile("remotechrome")
    public static WebDriver getRemoteWebDriver(@Value("${docker.chromedriver.url}") String dockerChomeDriverUrl) throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("-sessionTimeout=10");
        //ChromiumOptions

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
//        options.setBinary("/dev/shm/chromium-browser");

        return new RemoteWebDriver(new URL(dockerChomeDriverUrl), options);
    }
}
