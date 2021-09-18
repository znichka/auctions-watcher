package page;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AuctionPage {
    String url;
    private HashSet<String> oldItems;

    public AuctionPage(String url) {
        this.url = url;

        try {
            oldItems = getAllItems().stream()
                    .map(ItemDescription::getId)
                    .collect(Collectors.toCollection(HashSet::new));
        } catch (Exception e) {
            oldItems = new HashSet<>();
        }
    }

    abstract List<ItemDescription> getAllItems();

    public List<ItemDescription> getNewItems() {
        try {
            return getAllItems().stream()
                    .filter(item -> oldItems.add(item.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
