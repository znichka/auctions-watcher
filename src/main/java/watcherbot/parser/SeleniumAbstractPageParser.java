package watcherbot.parser;

import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import watcherbot.driver.AutoCloseableWebDriver;

import java.io.IOException;
import java.time.Duration;

@Component
@Log
public abstract class SeleniumAbstractPageParser extends AbstractPageParser  {
    @Autowired
    private ObjectFactory<AutoCloseableWebDriver> webDriverProvider;

    protected abstract ExpectedCondition<WebElement> expectedCondition();

    @Override
    protected synchronized Document getDocument(String url) throws IOException {
        try (AutoCloseableWebDriver driver = webDriverProvider.getObject()) {
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(expectedCondition());
            return Jsoup.parse(driver.getPageSource());
        } catch (BeansException e) {
            log.warning(String.format("Web driver for parser %s is not available, falling back to default parsing method", this.getDomainName()));
            return super.getDocument(url);
        } catch (Exception e){
            log.warning(String.format("Error while parsing the page, possible timeout. Url: %s", url));
            throw e;
        }
    }
}
