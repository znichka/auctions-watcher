import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import page.AuctionPage;
import page.AyPage;
import page.KufarPage;
import page.MeshokPage;

import javax.naming.directory.InvalidAttributesException;
import java.io.IOException;
import java.net.URL;
import java.util.*;


@NoArgsConstructor
@Setter
@Getter
public class PageWatcherConfig {
    @NoArgsConstructor
    @Getter
    @Setter
    static class PageDescription {
        private String url;
        private String description;
        private Integer period;
    }

    private String token;
    private String userId;
    private List<PageDescription> pages;

    public static List<PageWatcher> generateWatchersByConfig(PageWatcherConfig config) throws IOException {
        List<PageWatcher> watchers = new ArrayList<>();
        TelegramBot bot = new TelegramBot(config.token, config.userId);

        for (PageDescription pageDescription : config.pages) {

            String host = new URL(pageDescription.url).getHost().toLowerCase(Locale.ROOT);
            AuctionPage auctionPage;
            try {
                if (host.contains("antiques.ay.by")) {
                    auctionPage = new AyPage(pageDescription.url);
                } else if (host.contains("meshok.net")) {
                    auctionPage = new MeshokPage(pageDescription.url);
                } else if (host.contains("kufar.by")) {
                    auctionPage = new KufarPage(pageDescription.url);
                } else {
                    throw new InvalidAttributesException("Unknown link type");
                }
                watchers.add(new PageWatcher(auctionPage, pageDescription.description, bot, pageDescription.period));
            } catch (InvalidAttributesException | IOException e) {
//                e.printStackTrace();
                bot.sendMessage("Unknown link type" + pageDescription.url + "\nCheck the configuration file\n");
            }
        }
        return watchers;
    }
}
