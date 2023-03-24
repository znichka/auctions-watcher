package watcherbot.parser.page;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import watcherbot.description.ItemDescription;
import watcherbot.parser.AbstractPageParser;

@Component
public class OlxPageParser extends AbstractPageParser {
    @Override
    public Elements getElementCardsList(Document doc) {
        return doc.select("div[data-cy^=l-card]");
    }

    @Override
    public ItemDescription getItemFromCard(Element card) {
        Element itemElement = card.selectFirst("a");
        if (itemElement != null) {
            Element imgElement = itemElement.selectFirst("img");
            String photoUrl = imgElement.attr("src");
            String caption = imgElement.attr("alt");

            String itemUrl = itemElement.attr("href");
            itemUrl = "https://www.olx.pl" + itemUrl;
            String id = itemUrl.substring(itemUrl.length() - 12, itemUrl.length() - 5);

            return new ItemDescription(id, itemUrl, photoUrl, caption);
        }
        return null;
    }

    @Override
    public String getDomainName() {
        return "olx";
    }
}
