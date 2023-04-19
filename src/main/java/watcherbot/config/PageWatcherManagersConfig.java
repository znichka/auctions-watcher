package watcherbot.config;

import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Log
@Configuration
@EnableScheduling
public class PageWatcherManagersConfig {
    @Bean
    public static ScheduledExecutorService getScheduledExecutorService() {
        ScheduledThreadPoolExecutor service = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        service.setRemoveOnCancelPolicy(true);
        return service;
    }
}
