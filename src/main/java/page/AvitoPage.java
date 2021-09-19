package page;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AvitoPage extends AuctionPage {

    public AvitoPage(String url) {
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
            Elements cards = doc.getElementsByClass("iva-item-root-Nj_hb");

            for (Element card : cards){
                try {
                    String id = card.attr("data-item-id");

                    Element linkElement = card.getElementsByClass("link-link-MbQDP").first();
                    String caption = linkElement.attr("title");
                    String itemUrl = "www.avito.ru" + linkElement.attr("href");
                    if (itemUrl.contains("?slocation")) {
                        itemUrl = itemUrl.substring(0, itemUrl.indexOf("?slocation"));
                    }

                    Element imageElement = card.getElementsByClass("photo-slider-list-xFf2c").first().selectFirst("li");
                    String imageUrl = imageElement.attr("data-marker");
                    imageUrl = imageUrl.substring(19);

                    items.add(new ItemDescription(id, itemUrl, imageUrl, caption));
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
