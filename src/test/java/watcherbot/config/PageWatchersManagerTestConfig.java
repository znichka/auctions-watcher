package watcherbot.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import watcherbot.bot.TelegramBotCredentials;
import watcherbot.bot.TelegramBotSender;
import watcherbot.watchers.PageWatchersManager;

import java.util.concurrent.ScheduledExecutorService;

@Configuration
@ComponentScan({"watcherbot.watchers"})
public class PageWatchersManagerTestConfig {
    @Autowired
    ObjectProvider<PageWatchersManager> pageWatchersManagerProvider;

    @Bean
    TelegramBotSender getMockTelegramBotSender() {
        return Mockito.mock(TelegramBotSender.class);
    }

    @Bean
    ScheduledExecutorService getMockScheduledExecutorService() {
        return Mockito.mock(ScheduledExecutorService.class);
    }

    public PageWatchersManager getPageWatcherManager(TelegramBotCredentials credentials, String name){
        return pageWatchersManagerProvider.getObject(credentials, name);
    }
}
