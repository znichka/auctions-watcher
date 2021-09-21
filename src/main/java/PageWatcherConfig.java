import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import page.*;

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
                } else if (host.contains("avito.ru")) {
                    auctionPage = new AvitoPage(pageDescription.url);
                } else if (host.contains("ebay")) {
                    auctionPage = new EbayPage(pageDescription.url);
                }else {
                    throw new InvalidAttributesException(String.format("Unknown link type for %s. Check the configuration file", pageDescription.description));
                }
                watchers.add(new PageWatcher(auctionPage, pageDescription.description, bot, pageDescription.period));
            } catch (InvalidAttributesException e) {
                bot.sendMessage(e.getMessage());
            }
        }
        return watchers;
    }
}
