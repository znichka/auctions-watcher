package watcherbot.description;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PageDescription {
    private String url;
    private String description;
    private Integer period;
    private Long notify = 24L;
}
