package page;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KufarPage extends AuctionPage {
    public KufarPage(String url) {
        super(url);
    }

    @Override
    public List<ItemDescription> getAllItems() {
        List<ItemDescription> items = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();

            Elements cards = doc.getElementsByClass("kf-jklc-a67b1");
            for (Element card : cards) {


                String itemUrl = card.attr("href");
                String id = itemUrl.substring(itemUrl.length() - 9);

                Element imgElement = card.getElementsByClass("kf-jklX-59ddc").first();

                String photoUrl = imgElement.attr("data-src");
                String caption = imgElement.attr("alt");

                items.add(new ItemDescription(id, itemUrl, photoUrl, caption));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }
}
