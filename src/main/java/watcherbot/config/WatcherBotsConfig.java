package watcherbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import watcherbot.bot.TelegramBotSender;
import watcherbot.description.BotCredentials;
import watcherbot.description.ConfigDescription;
import watcherbot.description.PageDescription;
import watcherbot.description.WatcherBotDescription;
import watcherbot.watchers.PageWatcher;
import watcherbot.watchers.WatcherBotManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Log
@Configuration
public class WatcherBotsConfig {
    private final static String CONFIG_JSON = "CONFIG_JSON";

    private final ParserFactory availibleParsers;
    private final TelegramBotSender sender;
    private final ScheduledExecutorService scheduledExecutorService;

    @Autowired
    public WatcherBotsConfig(ParserFactory availibleParsers, TelegramBotSender sender, ScheduledExecutorService scheduledExecutorService) {
        this.availibleParsers = availibleParsers;
        this.sender = sender;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Bean("configDescription")
    @Profile("local")
    public ConfigDescription getLocalConfigDescription() throws IOException {
        File file = new File("config-local-run.json");
        if (file.exists()) {
            return new ObjectMapper().readValue(file, ConfigDescription.class);
        } else {
            throw new FileNotFoundException("config-local-run.json");
        }
    }

    @Bean("configDescription")
    @Profile("!local")
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
    public void configureWatchers(ConfigDescription config) {
        for (WatcherBotDescription watcherBotDescription : config.getWatchers()) {
            BotCredentials credentials = new BotCredentials(watcherBotDescription.getToken(), config.getUserId());
            WatcherBotManager manager = new WatcherBotManager(sender, credentials);

            for (PageDescription pageDescription : watcherBotDescription.getPages()) {
                try {
                    PageWatcher pageWatcher = new PageWatcher(availibleParsers.getParserFor(pageDescription.getUrl()), pageDescription, manager);
                    pageWatcher.schedule(scheduledExecutorService);
                    manager.registerPageWatcher(pageWatcher);
                } catch (Exception e) {
                    log.severe(e.getMessage());
                }
            }
        }
    }
}
