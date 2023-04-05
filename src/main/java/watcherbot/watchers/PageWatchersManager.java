package watcherbot.watchers;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import watcherbot.bot.TelegramBotCredentials;
import watcherbot.bot.TelegramBotSender;
import watcherbot.description.ItemDescription;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MINUTES;

@Log
@Component
@Scope("prototype")
public class PageWatchersManager  {
    @Autowired
    private TelegramBotSender sender;
    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    private final TelegramBotCredentials credentials;
    private final List<PageWatcher> registeredPageWatchers;

//    @Value("${healthcheck.fixedRate.in.milliseconds}")
    int healthCheckFixedRate;

    @Getter
    private final String name;

    private final HashSet<String> sentItemsIds;
    private final HashSet<String> sentItemsHashes;
//    private final HashSet<String> sentItems;

    public PageWatchersManager(TelegramBotCredentials credentials, String name) {
        this.credentials = credentials;
        this.name = name;

        sentItemsIds = new HashSet<>();
        sentItemsHashes = new HashSet<>();
        registeredPageWatchers = new ArrayList<>();
    }

    public synchronized List<ItemDescription> deleteAlreadySentItems(List<ItemDescription> items){
        return items.stream().filter(item ->  !sentItemsIds.contains(item.getId()))
                      .filter(item -> item.getPhotoHash() == null || !sentItemsHashes.contains(item.getPhotoHash()))
                      .collect(Collectors.toList());
    }

    private synchronized void loadHistory(PageWatcher pageWatcher) {
        pageWatcher.getNewItems().forEach(item -> {
            sentItemsIds.add(item.getId());
            sentItemsHashes.add(item.getPhotoHash());
            });
    }

    public synchronized void registerPageWatcher(PageWatcher pageWatcher){
        Runnable runnable = () -> {
            try {
                log.info(String.format("Run for a page - %s", pageWatcher.getDescription()));
                List<ItemDescription> newItems = deleteAlreadySentItems(pageWatcher.getNewItems());
                if (newItems.size() != 0) {
                    log.info(String.format("New items for %s page for %s bot", pageWatcher.getDescription(), getName()));
                    send(newItems);
                }
            } catch (Exception e) {
                log.severe(String.format("Error while parsing the page. Url: %s", pageWatcher.getDescription()));
                log.severe(e.getMessage());
            }
        };

        loadHistory(pageWatcher);
        scheduledExecutorService.scheduleAtFixedRate(runnable, 0, pageWatcher.getPeriod(), MINUTES);

        registeredPageWatchers.add(pageWatcher);
        send("Page watcher for " + pageWatcher.getDescription() + " has been added");

    }

    public synchronized void send(List<ItemDescription> items) {
        log.info("Update for the " + name + " bot");
        for(ItemDescription item : items) {
            try  {
                sender.sendItemDescription(credentials, item);
                sentItemsIds.add(item.getId());
                sentItemsHashes.add(item.getPhotoHash());
            } catch (IOException e) {
                log.severe(String.format("Error while sending item details to telegram bot %s. Item photo url: %s, item url: %s", name, item.getPhotoUrl(), item.getItemUrl()));
            }
        }
    }

    public synchronized void send(String message) {
        try {
            sender.sendMessage(credentials, message);
        } catch (IOException e) {
            log.severe(String.format("Error while sending message to telegram bot %s. Message: %s", name, message));
        }
    }

    @Scheduled(fixedRateString = "${healthcheck.fixedRate.in.milliseconds}")
    public void run() {
        log.info(String.format("Performing healthcheck for %s bot", name));

//        int healthCheckRate = healthCheckFixedRate/60000;

        for (PageWatcher pageWatcher : registeredPageWatchers) {
            try {
                long timeout = ChronoUnit.MINUTES.between(pageWatcher.getTimestamp(), LocalDateTime.now());
                long notifyHours = pageWatcher.getNotify();

                if (timeout >= (notifyHours * 60)) {
                    timeout %= (notifyHours * 60);
                    if (timeout <= healthCheckFixedRate) {
                        String message = String.format("There were no updates for %s for last %d hours",
                                pageWatcher.getDescription(),
                                pageWatcher.getNotify());
                        send(message);
                        log.info(message);
                    }
                }
            } catch (Exception e) {
                log.warning("Error while running a health check for " + name);
                log.warning(e.getMessage());
            }
        }
        log.info(String.format("Healthcheck finished for %s bot", name));
    }

    void setHealthCheckFixedRate(@Value("${healthcheck.fixedRate.in.milliseconds}") int fixedRate){
        this.healthCheckFixedRate = fixedRate/60000;
    }
}
