import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

public class Scheduler {
    private final static ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    private final static String CONFIG_JSON = "CONFIG_JSON";

    public static void main(String[] args) throws IOException {
        String json = Optional.ofNullable(System.getenv(CONFIG_JSON)).orElseThrow(
                () -> new InvalidObjectException(CONFIG_JSON + " is not set in the environment"));

        PageWatcherConfig config = new ObjectMapper().readValue(json, PageWatcherConfig.class);
        PageWatcherConfig.generateWatchersByConfig(config)
                         .forEach(watcher -> scheduler.scheduleAtFixedRate(watcher, 0, config.getTimeout(), MINUTES));

    }
}
