package watcherbot.parser;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import watcherbot.description.ItemDescription;
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
    public ItemDescription getItemFromCard(Element card) {
        String caption = card.attr("title");
        String itemUrl = card.attr("href");
        String id = card.attr("data-listing-id");

        Element imgElement = card.getElementsByTag("img").first();
        String photoUrl = imgElement.attr("src");

        return new ItemDescription(id, itemUrl, photoUrl, caption);
    }

    @Override
    public String getDomainName() {
        return "etsy";
    }
}
