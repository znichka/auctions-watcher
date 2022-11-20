package watcherbot.description;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class ConfigDescription {
    private List<PageWatchersManagerDescription> watchers;
    private String userId;
}
