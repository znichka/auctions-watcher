package page;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class EtsyPage extends AuctionPage{
    public EtsyPage(String url) {
        super(url);
    }

    @Override
    public List<ItemDescription> getAllItems() {
        List<ItemDescription> items = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(1000*5)
                    .get();

            Elements cards = doc.getElementsByClass("listing-link");
            for (Element card : cards) {
                try {
                    String caption = card.attr("title");
                    String itemUrl = card.attr("href");
                    String id = card.attr("data-listing-id");

                    Element imgElement = card.getElementsByTag("img").first();
                    String photoUrl = imgElement.attr("src");

                    items.add(new ItemDescription(id, itemUrl, photoUrl, caption));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
