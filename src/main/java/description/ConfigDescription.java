package description;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class ConfigDescription {
    private String token;
    private String userId;
    private List<PageDescription> pages;
}
