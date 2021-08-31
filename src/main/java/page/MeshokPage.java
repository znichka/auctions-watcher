package page;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import page.AuctionPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MeshokPage extends AuctionPage {
    public MeshokPage(String url) {
        super(url);
    }

    @Override
    public List<ItemDescription> getAllItems(){
        List<ItemDescription> items = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(1000*5)
                    .get();

            Elements cards = doc.getElementsByClass("itemCardList_2DhxS");

            for (Element card : cards) {
                Element cardImage = card.getElementsByClass("m-item-card-image image_2zt3d").first();
                if (cardImage != null) {
                    Element itemTitleElement = card.getElementsByClass("itemTitle_2gcl1").first();
                    String id = itemTitleElement.attr("data-itemcard");

                    String itemUrl = cardImage.attr("href");
                    Element imgElement = cardImage.selectFirst("img");
                    String photoUrl = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg";
                    if (imgElement != null) photoUrl = imgElement.attr("src");
                    String caption = itemTitleElement.text();
                    itemUrl = "http://meshok.net" + itemUrl;

                    items.add(new ItemDescription(id, itemUrl, photoUrl, caption));

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }
}
