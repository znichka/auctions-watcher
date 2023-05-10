package watcherbot.parser;

import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
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

    protected boolean scroll = false;

    @Override
    protected Document getDocument(String url) throws IOException {
        try (AutoCloseableWebDriver driver = webDriverProvider.getObject()) {
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            if (scroll) {
                int pageCount = 10;
                for (int i=0;i<pageCount;i++)
                    new Actions(driver).keyDown(Keys.SPACE).pause(1000).keyUp(Keys.SPACE).perform();
            }

            try
            {
                wait.until(expectedCondition());
            } catch (Exception e) {
                LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
                logs.getAll().forEach(l -> log.warning(l.getMessage()));
                throw e;
            }
            return Jsoup.parse(driver.getPageSource());
        } catch (BeansException e) {
            log.warning(String.format("Web driver for parser %s is not available, falling back to default parsing method", this.getDomainName()));
            return super.getDocument(url);
        } catch (Exception e){
            log.warning(String.format("Error while parsing the page, possible timeout. Url: %s", url));
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
