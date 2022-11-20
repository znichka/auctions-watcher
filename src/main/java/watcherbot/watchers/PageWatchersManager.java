package watcherbot.watchers;

import lombok.extern.java.Log;
import watcherbot.bot.TelegramBotSender;
import watcherbot.description.TelegramBotCredentials;
import watcherbot.description.PageItemDescription;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

@Log
public class PageWatchersManager implements Runnable {
    private final TelegramBotSender sender;
    private final TelegramBotCredentials credentials;

    private final Map<PageWatcher, LocalDateTime> pageWatchers;
    private final HashSet<String> sentItems;

    public PageWatchersManager(TelegramBotSender sender, TelegramBotCredentials credentials) {
        this.sender = sender;
        this.credentials = credentials;

        sentItems = new HashSet<>();
        pageWatchers = new HashMap<>();
    }

    public void registerPageWatcher(PageWatcher watcher) {
        pageWatchers.put(watcher, LocalDateTime.now());
    }

    public void schedulePageWatchers(ScheduledExecutorService scheduledExecutorService) {
        for(PageWatcher pageWatcher : pageWatchers.keySet()) {
            scheduledExecutorService.scheduleAtFixedRate(pageWatcher, 0, pageWatcher.getPageDescription().getPeriod(), MINUTES);

            String message = String.format("Watcher for %s has been scheduled", pageWatcher.getPageDescription().getDescription());
            send(message);
            log.info(message);
        }
    }

    public void sendUpdate(PageWatcher pageWatcher){
        List<PageItemDescription> newItems = pageWatcher.getNewItems();
        if (newItems.size() != 0) {
            send(newItems);
            pageWatchers.put(pageWatcher, LocalDateTime.now());
        }
    }

    public void send(List<PageItemDescription> items) {
        for(PageItemDescription item : items) {
            if (!sentItems.contains(item.getId())) {
                try {
                    sender.sendImageUpload(credentials, item.getPhotoUrl(), item.getCaption(), item.getItemUrl());
                } catch (IOException e) {
                    log.severe(String.format("Error while sending item details to telegram bot. Item photo url: %s, item url: %s", item.getPhotoUrl(), item.getItemUrl()));
                }
                sentItems.add(item.getId());
            }
        }
    }

    public void send(String message) {
        try {
            sender.sendMessage(credentials, message);
        } catch (IOException e) {
            log.severe(String.format("Error while sending message to telegram bot. Message: %s", message));
        }
    }

    @Override
    public void run() {
        try {
            for (Map.Entry<PageWatcher, LocalDateTime> entry : pageWatchers.entrySet()) {
                long timeout = ChronoUnit.HOURS.between(entry.getValue(), LocalDateTime.now());

                if (timeout >= entry.getKey().getPageDescription().getNotify()) {
                    String message = String.format("There were no updates for %s for last %d hours",
                                                    entry.getKey().getPageDescription().getDescription(),
                                                    entry.getKey().getPageDescription().getNotify());
                    send(message);
                    log.info(message);
                }
            }
        } catch (Exception e) {
            log.severe(String.format("Error while running a health check"));
            log.severe(e.getMessage());
        }
    }
}
