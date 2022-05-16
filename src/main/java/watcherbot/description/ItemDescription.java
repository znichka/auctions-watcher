package watcherbot.description;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ItemDescription {
    String id;
    String itemUrl;
    String photoUrl;
    String caption;
}
