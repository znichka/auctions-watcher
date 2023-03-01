package watcherbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import watcherbot.bot.TelegramBotCredentials;
import watcherbot.bot.TelegramBotSender;
import watcherbot.description.ConfigDescription;
import watcherbot.description.PageDescription;
import watcherbot.description.PageWatchersManagerDescription;
import watcherbot.parser.PageParserFactory;
import watcherbot.watchers.PageWatchersManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Log
@Configuration
public class PageWatcherManagersConfig {
    private final static String CONFIG_JSON = "CONFIG_JSON";

    private final PageParserFactory availableParsers;
    private final TelegramBotSender sender;
    private final ScheduledExecutorService scheduledExecutorService;

    @Autowired
    public PageWatcherManagersConfig(PageParserFactory availableParsers, TelegramBotSender sender, ScheduledExecutorService scheduledExecutorService) {
        this.availableParsers = availableParsers;
        this.sender = sender;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Bean("configDescription")
    @Profile({"local", "remotechrome"})
    public static ConfigDescription getLocalConfigDescription() throws IOException {
        File file = new File("config-local-run.json");
        if (file.exists()) {
            return new ObjectMapper().readValue(file, ConfigDescription.class);
        } else {
            throw new FileNotFoundException("config-local-run.json");
        }
    }

    @Bean("configDescription")
    @Profile({"!local & !remotechrome" })
    public static ConfigDescription getEnvConfigDescription() throws IOException {
        String json = Optional.ofNullable(System.getenv(CONFIG_JSON)).orElseThrow(
                () -> new InvalidObjectException(CONFIG_JSON + " is not set in the environment"));
        return new ObjectMapper().readValue(json, ConfigDescription.class);
    }

    @Bean
    public static ScheduledExecutorService getScheduledExecutorService() {
        return Executors.newScheduledThreadPool(1);
    }

    @Autowired
    public void configurePageWatchers(ConfigDescription config) {
        for (PageWatchersManagerDescription pageWatchersManagerDescription : config.getWatchers()) {
            TelegramBotCredentials credentials = new TelegramBotCredentials(pageWatchersManagerDescription.getToken(), config.getUserId());
            PageWatchersManager manager = new PageWatchersManager(sender, credentials, scheduledExecutorService, pageWatchersManagerDescription.getName());

            for (PageDescription pageDescription : pageWatchersManagerDescription.getPages()) {
                try {
                    manager.registerPageWatcher(availableParsers.getParserFor(pageDescription.getUrl()), pageDescription);
                } catch (Exception e) {
                    log.severe(e.getMessage());
                }
            }
        }
    }
}
