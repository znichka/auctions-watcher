package watcherbot.config;

import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import watcherbot.bot.TelegramBotSender;
import watcherbot.repository.PageWatcherRepository;
import watcherbot.repository.PageWatchersManagerRepository;
import watcherbot.service.PageWatcherService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@ComponentScan({"watcherbot.controller",
                "watcherbot.parser",
                "watcherbot.service",
                "watcherbot.watchers"})
public class ConfigurationControllerTestConfig {
    @Bean
    public static ScheduledExecutorService getScheduledExecutorService() {
        return Executors.newScheduledThreadPool(1);
    }

    @Bean
    public PageWatchersManagerRepository getMockPageWatchersManagerRepository() {
        return Mockito.mock(PageWatchersManagerRepository.class);
    }

    @Bean
    public PageWatcherRepository getMockPageWatcherRepository() {
        return Mockito.mock(PageWatcherRepository.class);
    }

    @Bean
    public TelegramBotSender getTelegramBotSender(){
        return Mockito.mock(TelegramBotSender.class);
    }

}
