package page;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AuctionPage {
    String url;
    private final HashSet<String> oldItems;

    public AuctionPage(String url) {
        this.url = url;

        oldItems = getAllItems().stream()
                .map(ItemDescription::getId)
                .collect(Collectors.toCollection(HashSet::new));
    }

    protected abstract List<ItemDescription> getAllItems();

    public List<ItemDescription> getNewItems() {
        return getAllItems().stream()
                .filter(item -> oldItems.add(item.getId()))
                .collect(Collectors.toList());
    }
}
