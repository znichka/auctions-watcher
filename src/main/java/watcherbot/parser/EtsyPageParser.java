package watcherbot.parser;

import org.springframework.stereotype.Component;
import watcherbot.description.PageItemDescription;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Component
public class EtsyPageParser extends PageParser {
    @Override
    public Elements getElementCardsList(Document doc) {
        return doc.getElementsByClass("listing-link");
    }

    @Override
    public PageItemDescription getItemFromCard(Element card) {
        String caption = card.attr("title");
        String itemUrl = card.attr("href");
        String id = card.attr("data-listing-id");

        Element imgElement = card.getElementsByTag("img").first();
        String photoUrl = imgElement.attr("src");

        return new PageItemDescription(id, itemUrl, photoUrl, caption);
    }

    @Override
    public String getDomainName() {
        return "etsy";
    }
}
