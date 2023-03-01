package watcherbot.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public abstract class SeleniumAbstractPageParser extends AbstractPageParser implements DisposableBean {
    WebDriver driver;

    protected abstract ExpectedCondition<WebElement> expectedCondition();

    @Override
    protected Document getDocument(String url) throws IOException {
        if (driver == null) return super.getDocument(url);

        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(expectedCondition());
        Document result = Jsoup.parse(driver.getPageSource());
        driver.close();

        return result;
    }

    @Autowired(required = false)
    public final void setDriver(WebDriver driver){
        this.driver = driver;
    }

    @Override
    public void destroy() {
        if (driver!= null) driver.quit();
    }
}
