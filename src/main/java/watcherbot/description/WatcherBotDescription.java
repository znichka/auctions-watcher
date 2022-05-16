package watcherbot.description;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class WatcherBotDescription {
    String token;
    String name;
    private List<PageDescription> pages;
}
