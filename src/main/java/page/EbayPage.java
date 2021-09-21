package page;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class EbayPage extends AuctionPage {
    public EbayPage(String url) {
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

            Elements cards = doc.getElementsByClass("s-item__wrapper");
            for (Element card : cards) {
                try {
                    Element imgElement = card.getElementsByClass("s-item__image-img").first();
                    String photoUrl = imgElement.attr("src");
                    String caption = imgElement.attr("alt");

                    Element linkElement = card.getElementsByClass("s-item__link").first();
                    String itemUrl = linkElement.attr("href");
                    String id = itemUrl.substring(25, 37);

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
