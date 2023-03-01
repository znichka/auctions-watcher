package watcherbot.parser;

import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import watcherbot.description.PageItemDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log
public abstract class AbstractPageParser {
    protected abstract Elements getElementCardsList(Document doc);

    protected abstract PageItemDescription getItemFromCard(Element card);

    public abstract String getDomainName();

    protected Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
//                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
//                    .userAgent("Opera")
                .referrer("http://www.google.com")
                .timeout(1000*5)
                .get();
    }

    public final List<PageItemDescription> getAllItems(String url) {
        List<PageItemDescription> items = new ArrayList<>();
        try {
            Document doc = getDocument(url);
            Elements cards = getElementCardsList(doc);
            for (Element card : cards){
                try {
                    PageItemDescription item = getItemFromCard(card);
                    if (item != null) items.add(item);
                } catch (Exception e) {
                    log.warning("Error while parsing an item on a page, skipping. Url: "+ url);
                }
            }
        } catch (Exception e) {
            log.warning("Error while parsing a page, skipping. Url: "+ url);
        }
        return items;
    }
}
