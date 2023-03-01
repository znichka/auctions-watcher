package watcherbot.watchers;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import watcherbot.description.PageDescription;
import watcherbot.description.PageItemDescription;
import watcherbot.parser.AbstractPageParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Log
public class PageWatcher implements Runnable  {
    @Getter
    private final PageDescription pageDescription;
    private final PageWatchersManager manager;
    private final AbstractPageParser parser;
    private HashSet<String> oldItems;

    public PageWatcher(AbstractPageParser parser, PageDescription pageDescription, PageWatchersManager manager) {
        this.pageDescription = pageDescription;
        this.parser = parser;
        this.manager = manager;
        init();
    }

    private List<PageItemDescription> parsePage() {
        return parser.getAllItems(pageDescription.getUrl());
    }

    private void init(){
        oldItems = new HashSet<>();
        try {
            getNewItems();
        } catch (Exception e) {
            log.warning(String.format("Error while initialising oldItems a in PageWatcher class. Page info: %s", pageDescription.getDescription()));
        }
    }

    public List<PageItemDescription> getNewItems() {
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
        try {
            log.info(String.format("Run for a page - %s", pageDescription.getDescription()));
            manager.sendUpdate(this);
        } catch (Exception e) {
            log.severe(String.format("Error while running a page check. Page url: %s", pageDescription.getDescription()));
            log.severe(e.getMessage());

        }
    }
}
