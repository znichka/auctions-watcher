package parser;

import description.ItemDescription;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public abstract class PageParser {
    protected abstract Elements getElementCardsList(Document doc);

    protected abstract ItemDescription getItemFromCard(Element card);

    public final List<ItemDescription> getAllItems(String url) {
        List<ItemDescription> items = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(1000*5)
                    .get();
            Elements cards = getElementCardsList(doc);
            for (Element card : cards){
                try {
                    ItemDescription item = getItemFromCard(card);
                    if (item != null) items.add(item);
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
