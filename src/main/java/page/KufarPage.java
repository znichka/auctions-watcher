package page;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

            Element article = doc.getElementsByTag("article").get(0);
            Elements cards = article.getElementsByTag("a");//doc.getElementsByClass("kf-GvOm-7afe3");
            for (Element card : cards) {
                try {
                    String itemUrl = card.attr("href");
                    String id = itemUrl.substring(itemUrl.length() - 9);

                    Element imgElement = card.getElementsByTag("img").first();

                    String photoUrl = imgElement == null ? "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg" : imgElement.attr("data-src");
                    String caption = imgElement == null ? "Елочная игрушка": imgElement.attr("alt");

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
