import com.fasterxml.jackson.databind.ObjectMapper;
import config.AuctionWatcherConfig;
import description.ConfigDescription;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class Scheduler {
    private final static ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    private final static String CONFIG_JSON = "CONFIG_JSON";

    public static void main(String[] args) throws IOException {
        String json = Optional.ofNullable(System.getenv(CONFIG_JSON)).orElseThrow(
                () -> new InvalidObjectException(CONFIG_JSON + " is not set in the environment"));
        ConfigDescription config = new ObjectMapper().readValue(json, ConfigDescription.class);

        AuctionWatcherConfig.generateAuctionWatchersByConfig(config)
                         .forEach(auctionWatcher -> auctionWatcher.schedulePages(scheduler));
    }
}
