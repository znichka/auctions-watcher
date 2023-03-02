package watcherbot.aspect;

import lombok.extern.java.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import watcherbot.parser.SeleniumAbstractPageParser;

import java.lang.reflect.Field;

@Aspect
@Component
@Log
public class WebDriverUpdaterAspect {
    @Autowired
    ApplicationContext applicationContext;

    @Around("execution(* watcherbot.parser.SeleniumAbstractPageParser.getDocument(..))")
    public Object updateWebDriver(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
    {
        WebDriver newWebDriver = (WebDriver) applicationContext.getBean("webDriver");
        SeleniumAbstractPageParser obj = (SeleniumAbstractPageParser) proceedingJoinPoint.getTarget();

        Field driverField = obj.getClass().getSuperclass()
                .getDeclaredField("driver");
        driverField.setAccessible(true);
        driverField.set(obj, newWebDriver);
        log.info(String.format("WebDriver for %s parser updated", obj.getDomainName()));

        try
        {
            return proceedingJoinPoint.proceed();
        }
        catch (Exception e){
            log.warning(e.getMessage());
            throw e;
        }
        finally {
            newWebDriver.quit();
            log.info(String.format("WebDriver for %s parser closed", obj.getDomainName()));
        }
    }
}
