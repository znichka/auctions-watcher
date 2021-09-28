package description;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class ConfigDescription {
    private HashMap<String, List<String>> tokens;
    private String userId;
    private List<PageDescription> pages;
}
