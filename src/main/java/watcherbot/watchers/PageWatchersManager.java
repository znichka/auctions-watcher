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
    private final String name;

    private final Map<PageWatcher, LocalDateTime> pageWatchers;
    private final HashSet<String> sentItems;
    private final int HEALTH_CHECK_TIMEOUT = 30;

    public PageWatchersManager(TelegramBotSender sender, TelegramBotCredentials credentials, ScheduledExecutorService scheduledExecutorService, String name) {
        this.sender = sender;
        this.credentials = credentials;
        this.scheduledExecutorService = scheduledExecutorService;
        this.name = name;
        scheduledExecutorService.scheduleAtFixedRate(this, 0, HEALTH_CHECK_TIMEOUT, TimeUnit.MINUTES);

        sentItems = new HashSet<>();
        pageWatchers = new HashMap<>();
    }

    public void registerPageWatcher(PageParser parser, PageDescription pageDescription) {
        PageWatcher watcher = new PageWatcher(parser, pageDescription, this);
        pageWatchers.put(watcher, LocalDateTime.now());
        scheduledExecutorService.scheduleAtFixedRate(watcher, 0, watcher.getPageDescription().getPeriod(), MINUTES);

        String message = String.format("Watcher for %s for %s bot has been scheduled", watcher.getPageDescription().getDescription(), name);
        send(message);
        log.info(message);
    }

    public synchronized void sendUpdate(PageWatcher pageWatcher){
        log.info("Update for " + name + "bot");
        List<PageItemDescription> newItems = pageWatcher.getNewItems();
        if (newItems.size() != 0) {
            log.info("New items for " + pageWatcher.getPageDescription().getDescription() + " for " + name + "bot");
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
                    log.severe(String.format("Error while sending item details to telegram bot %s. Item photo url: %s, item url: %s", name, item.getPhotoUrl(), item.getItemUrl()));
                }
                sentItems.add(item.getId());
            }
        }
    }

    public void send(String message) {
        try {
            sender.sendMessage(credentials, message);
        } catch (IOException e) {
            log.severe(String.format("Error while sending message to telegram bot %s. Message: %s", name, message));
        }
    }

    @Override
    public void run() {
        try {
            for (Map.Entry<PageWatcher, LocalDateTime> entry : pageWatchers.entrySet()) {
                long timeout = ChronoUnit.MINUTES.between(entry.getValue(), LocalDateTime.now());
                long notifyHours = entry.getKey().getPageDescription().getNotify();

                if (timeout >= (notifyHours * 60)) {
                    timeout %= (notifyHours * 60);
                    if (timeout <= HEALTH_CHECK_TIMEOUT) {
                        String message = String.format("There were no updates for %s for last %d hours",
                                entry.getKey().getPageDescription().getDescription(),
                                entry.getKey().getPageDescription().getNotify());
                        send(message);
                        log.info(message);
                    }
                }
            }
        } catch (Exception e) {
            log.severe(String.format("Error while running a health check for %s bot", name));
            log.severe(e.getMessage());
        }
    }
}
