package watcherbot.watchers;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import watcherbot.description.ItemDescription;
import watcherbot.description.PageDescription;
import watcherbot.parser.PageParser;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MINUTES;

@Log
public class PageWatcher implements Runnable  {
    private final PageDescription pageDescription;
    private final WatcherBotManager manager;
    private final PageParser parser;
    private HashSet<String> oldItems;

    private LocalDateTime lastUpdate;
    private static final Long TIMEOUT_HOURS = 6L;
    private Long timeoutCheck = TIMEOUT_HOURS;

    public PageWatcher(PageParser parser, PageDescription pageDescription, WatcherBotManager manager) {
        this.pageDescription = pageDescription;
        this.parser = parser;
        this.manager = manager;
        init();
    }

    public void schedule(ScheduledExecutorService scheduledExecutorService){
        scheduledExecutorService.scheduleAtFixedRate(this, 0, pageDescription.getPeriod(), MINUTES);

        String message = String.format("Watcher for %s has been scheduled", pageDescription.getDescription());
        manager.send(message);
        log.info(message);
    }

    private List<ItemDescription> parsePage() {
        return parser.getAllItems(pageDescription.getUrl());
    }

    private void init(){
        oldItems = new HashSet<>();
        try {
            getNewItems();
        } catch (Exception e) {
            log.warning(String.format("Error while initialising oldItems a in PageWatcher class. Page info: %s", pageDescription.getDescription()));
        }
        lastUpdate = LocalDateTime.now();
    }

    private List<ItemDescription> getNewItems() {
        try {
            return parsePage().stream()
                    .filter(item -> oldItems.add(item.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warning(String.format("Error while getting new items for a page. Page info: %s", pageDescription.getDescription()));
            return new ArrayList<>();
        }
    }

    @SneakyThrows
    public void run() {
        log.info(String.format("Run for a page - %s", pageDescription.getDescription()));

        List<ItemDescription> newItems = getNewItems();

        if (newItems.size() == 0) {
            long timeout = ChronoUnit.HOURS.between(lastUpdate, LocalDateTime.now());
            if (timeout >= timeoutCheck) {
                String message = String.format("There were no updates for %s for %d hours", pageDescription.getDescription(), timeoutCheck);
                manager.send(message);
                log.info(message);

                timeoutCheck += TIMEOUT_HOURS;
            }
        } else {
            lastUpdate = LocalDateTime.now();
            timeoutCheck = TIMEOUT_HOURS;

            manager.send(newItems);
        }
    }
}
