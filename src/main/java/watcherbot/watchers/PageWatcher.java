package watcherbot.watchers;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.Id;
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
    @Getter
    final PageDescription description;

    @Getter
//    private final PageWatchersManager manager;
    private final AbstractPageParser parser;
    private final HashSet<String> oldItems;
    @Getter
    private LocalDateTime timestamp;

    public PageWatcher(AbstractPageParser parser, PageDescription pageDescription/*, PageWatchersManager manager*/) {
        this.description = pageDescription;

        this.parser = parser;
        oldItems = new HashSet<>();
        getNewItems();
    }

    public List<ItemDescription> getNewItems() {
        try {
            List<ItemDescription> items = parser.getAllItems(description.getUrl()).stream()
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
}
