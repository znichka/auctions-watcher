package page;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AyPage extends AuctionPage {
    public AyPage(String url) {
        super(url);
    }

    @Override
    public List<ItemDescription> getAllItems() {
        List<ItemDescription> items = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();

            Elements cards = doc.select("li.viewer-type-grid__li[data-value] ");
            for (Element card : cards) {
                String id = card.attr("data-value");

                Element item = card.selectFirst("a.item-type-card__link");
                String itemUrl = item.attr("href");
                String photoUrl = item.selectFirst("img").attr("src");
                String caption = item.getElementsByClass("item-type-card__title").first().text();
                items.add(new ItemDescription(id, itemUrl, photoUrl, caption));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }
}
