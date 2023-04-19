package watcherbot.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.java.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import watcherbot.driver.AutoCloseableWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Log
@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("watcherbot.driver")
public class WebDriverConfig {
    @Bean(destroyMethod = "quit", name = "webDriver")
    @Scope("prototype")
    @Profile("local")
    public static AutoCloseableWebDriver getLocalWebDriver() {
        WebDriverManager.chromedriver().setup();
        return new AutoCloseableWebDriver(new ChromeDriver());
    }

    @Bean(destroyMethod = "quit", name = "webDriver")
    @Scope("prototype")
    @Profile({"!local"})
    public static AutoCloseableWebDriver getRemoteWebDriver(@Value("${docker.chromedriver.url}") String dockerChomeDriverUrl ) throws ExecutionException, InterruptedException {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = CompletableFuture.supplyAsync(() -> {
            try {
                return new RemoteWebDriver(new URL(dockerChomeDriverUrl), options);
            } catch (MalformedURLException e) {
                log.severe(String.format("Web driver not created, url is %s, exception message is %s", dockerChomeDriverUrl, e.getMessage()));
                return null;
            }
        }).completeOnTimeout(null,10, TimeUnit.SECONDS).exceptionally(t -> {
            log.severe(String.format("Web driver not created, url is %s, exception message is %s", dockerChomeDriverUrl, t.getMessage()));
            return null;
        }).get();

        if (driver == null) log.severe("Web driver not created, possible timeout. Url is " + dockerChomeDriverUrl);
        return new AutoCloseableWebDriver(driver);
    }
}
