import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import page.AyPage;
import page.KufarPage;
import page.MeshokPage;

import javax.naming.directory.InvalidAttributesException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageWatcherConfig {
    private String token;
    private String userId;
    private List<String> urls;
    private Integer timeout;

    public static List<PageWatcher> generateWatchersByConfig(PageWatcherConfig config) throws MalformedURLException {
        List<PageWatcher> watchers = new ArrayList<>();
        TelegramBot bot = new TelegramBot(config.getToken(), config.getUserId());

        for (String url : config.getUrls()) {
            String host = new URL(url).getHost();
            try {
                if (host.toLowerCase(Locale.ROOT).equals("antiques.ay.by")) {
                    watchers.add(new PageWatcher(new AyPage(url), bot));
                } else if (host.toLowerCase(Locale.ROOT).equals("meshok.net")) {
                    watchers.add(new PageWatcher(new MeshokPage(url), bot));
                } else if (host.toLowerCase(Locale.ROOT).equals("www.kufar.by")) {
                    watchers.add(new PageWatcher(new KufarPage(url), bot));
                } else {
                    throw new InvalidAttributesException("Unknown link type");
                }
            } catch (InvalidAttributesException | IOException e) {
                e.printStackTrace();
            }
        }
        return watchers;
    }
}
