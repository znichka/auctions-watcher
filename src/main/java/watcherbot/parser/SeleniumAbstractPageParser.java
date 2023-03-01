package watcherbot.parser;

import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.Duration;

@Component
@Log
public abstract class SeleniumAbstractPageParser extends AbstractPageParser  {
    WebDriver driver;

    protected abstract ExpectedCondition<WebElement> expectedCondition();

    @Override
    protected synchronized Document getDocument(String url) throws IOException {
        if (driver == null) {
            log.warning(String.format("Web driver for parser %s is not available, falling back to default parsing method", this.getDomainName()));
            return super.getDocument(url);
        }

        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(expectedCondition());
//        driver.close();

        return Jsoup.parse(driver.getPageSource());
    }

    @PreDestroy
    public void destroy() {
        if (driver!= null) driver.quit();
        System.out.println("destroy");
    }
}
