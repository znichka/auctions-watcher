package watcherbot.description;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.net.URL;

@NoArgsConstructor
@Getter
@Setter
public class PageDescription {
    private String url;
    private String description;
    private Integer period;
}
