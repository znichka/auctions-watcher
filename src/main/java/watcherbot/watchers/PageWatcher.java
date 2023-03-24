package watcherbot.watchers;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import watcherbot.description.PageDescription;
import watcherbot.description.ItemDescription;
import watcherbot.parser.AbstractPageParser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Component
@Scope("prototype")
public class PageWatcher {
    private final String url;
    @Getter
    private final String description;
    @Getter
    private final Integer period;
    @Getter
    private final Long notify;

    @Getter
//    private final PageWatchersManager manager;
    private final AbstractPageParser parser;
    private final HashSet<String> oldItems;
    @Getter
    private LocalDateTime timestamp;

    public PageWatcher(AbstractPageParser parser, PageDescription pageDescription/*, PageWatchersManager manager*/) {
        this.description = pageDescription.getDescription();
        this.period = pageDescription.getPeriod();
        this.url = pageDescription.getUrl();
        this.notify = pageDescription.getNotify();

        this.parser = parser;
//        this.manager = manager;

//        manager.registerPageWatcher(this);
        oldItems = new HashSet<>();
        getNewItems();
//        manager.send("Page watcher for "+pageDescription.getDescription() + "has been added");
    }

    public List<ItemDescription> getNewItems() {
        try {
            List<ItemDescription> items = parser.getAllItems(url).stream()
                    .filter(item -> oldItems.add(item.getId()))
                    .collect(Collectors.toList());
            if (items.size() != 0)
                timestamp = LocalDateTime.now();
            return items;
        } catch (Exception e) {
            log.warning(String.format("Error while getting new items for a page. Page info: %s", description));
            return new ArrayList<>();
        }
    }

//    @SneakyThrows
//    public void run() {
//        try {
//            log.info(String.format("Run for a page - %s", pageDescription.getDescription()));
//            List<PageItemDescription> newItems = getNewItems();
//            if (newItems.size() != 0) {
//                log.info(String.format("New items for %s page for %s bot", pageDescription.getDescription(), manager.getName()));
//                manager.send(newItems);
//            }
//        } catch (Exception e) {
//            log.severe(String.format("Error while parsing the page. Url: %s", pageDescription.getDescription()));
//            log.severe(e.getMessage());
//        }
//    }
}
