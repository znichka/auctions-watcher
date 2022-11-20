package watcherbot.description;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PageItemDescription {
    String id;
    String itemUrl;
    String photoUrl;
    String caption;
}
