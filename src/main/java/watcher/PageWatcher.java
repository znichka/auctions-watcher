package watcher;

import description.PageDescription;
import lombok.SneakyThrows;
import parser.PageParser;
import description.ItemDescription;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class PageWatcher implements Runnable {
    AuctionWatcher auctionWatcher;
    PageDescription pageDescription;
    private HashSet<String> oldItems;

    LocalDateTime lastUpdate;
    final static Long TIMEOUT_HOURS = 6L;
    Long timeoutCheck = TIMEOUT_HOURS;

    PageWatcher(AuctionWatcher auctionWatcher, PageDescription pageDescription) throws IOException {
        this.auctionWatcher = auctionWatcher;
        this.pageDescription = pageDescription;

        try {
            oldItems = auctionWatcher.parse(pageDescription.getUrl()).stream()
                    .map(ItemDescription::getId)
                    .collect(Collectors.toCollection(HashSet::new));
        } catch (Exception e) {
            oldItems = new HashSet<>();
        }

        auctionWatcher.send(String.format("Watcher for %s has started", pageDescription.getDescription()));
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
            return new ArrayList<>();
        }
    }

    @SneakyThrows
    public void run() {
        List<ItemDescription> newItems = getNewItems();
        if (newItems.size() == 0) {
            Long timeout = ChronoUnit.HOURS.between(lastUpdate, LocalDateTime.now());
            if (timeout >= timeoutCheck) {
                auctionWatcher.send(String.format("There were no updates for %s for %d hours", pageDescription.getDescription(), timeoutCheck));
                timeoutCheck += TIMEOUT_HOURS;
            }
        } else {
            lastUpdate = LocalDateTime.now();
            timeoutCheck = TIMEOUT_HOURS;

            auctionWatcher.send(newItems);
        }
    }
}
