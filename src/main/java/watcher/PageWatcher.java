package watcher;

import description.ItemDescription;
import description.PageDescription;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Log
public class PageWatcher implements Runnable {
    AuctionWatcher auctionWatcher;
    PageDescription pageDescription;
    private HashSet<String> oldItems;

    LocalDateTime lastUpdate;
    final static Long TIMEOUT_HOURS = 6L;
    Long timeoutCheck = TIMEOUT_HOURS;

    PageWatcher(AuctionWatcher auctionWatcher, PageDescription pageDescription) {
        this.auctionWatcher = auctionWatcher;
        this.pageDescription = pageDescription;

        try {
            oldItems = auctionWatcher.parse(pageDescription.getUrl()).stream()
                    .map(ItemDescription::getId)
                    .collect(Collectors.toCollection(HashSet::new));
        } catch (Exception e) {
            oldItems = new HashSet<>();
            log.warning(String.format("Error while initialising oldItems a in PageWatcher class. Page info: %s", pageDescription.getDescription()));
        }

        auctionWatcher.send(String.format("Watcher for %s has started", pageDescription.getDescription()));
        log.info(String.format("Watcher for %s has started", pageDescription.getDescription()));
        lastUpdate = LocalDateTime.now();
    }

    public int getPeriod(){
        return pageDescription.getPeriod();
    }

    private List<ItemDescription> getNewItems() {
        try {
            return auctionWatcher.parse(pageDescription.getUrl()).stream()
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
            Long timeout = ChronoUnit.HOURS.between(lastUpdate, LocalDateTime.now());
            if (timeout >= timeoutCheck) {
                auctionWatcher.send(String.format("There were no updates for %s for %d hours", pageDescription.getDescription(), timeoutCheck));
                log.info(String.format("There were no updates for %s for %d hours", pageDescription.getDescription(), timeoutCheck));

                timeoutCheck += TIMEOUT_HOURS;
            }
        } else {
            lastUpdate = LocalDateTime.now();
            timeoutCheck = TIMEOUT_HOURS;

            auctionWatcher.send(newItems);
        }
    }
}
