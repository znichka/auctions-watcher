package watcherbot.watchers;

import lombok.extern.java.Log;
import watcherbot.bot.TelegramBotCredentials;
import watcherbot.bot.TelegramBotSender;
import watcherbot.description.PageDescription;
import watcherbot.description.PageItemDescription;
import watcherbot.parser.PageParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;

@Log
public class PageWatchersManager implements Runnable {
    private final TelegramBotSender sender;
    private final TelegramBotCredentials credentials;
    private final ScheduledExecutorService scheduledExecutorService;

    private final Map<PageWatcher, LocalDateTime> pageWatchers;
    private final HashSet<String> sentItems;
    private final int HEALTH_CHECK_TIMEOUT = 30;

    public PageWatchersManager(TelegramBotSender sender, TelegramBotCredentials credentials, ScheduledExecutorService scheduledExecutorService) {
        this.sender = sender;
        this.credentials = credentials;
        this.scheduledExecutorService = scheduledExecutorService;
        scheduledExecutorService.scheduleAtFixedRate(this, 0, HEALTH_CHECK_TIMEOUT, TimeUnit.MINUTES);

        sentItems = new HashSet<>();
        pageWatchers = new HashMap<>();
    }

    public void registerPageWatcher(PageParser parser, PageDescription pageDescription) {
        PageWatcher watcher = new PageWatcher(parser, pageDescription, this);
        pageWatchers.put(watcher, LocalDateTime.now());
        scheduledExecutorService.scheduleAtFixedRate(watcher, 0, watcher.getPageDescription().getPeriod(), MINUTES);

        String message = String.format("Watcher for %s has been scheduled", watcher.getPageDescription().getDescription());
        send(message);
        log.info(message);
    }

    public synchronized void sendUpdate(PageWatcher pageWatcher){
        List<PageItemDescription> newItems = pageWatcher.getNewItems();
        if (newItems.size() != 0) {
            send(newItems);
            pageWatchers.put(pageWatcher, LocalDateTime.now());
        }
    }

    public synchronized void send(List<PageItemDescription> items) {
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
