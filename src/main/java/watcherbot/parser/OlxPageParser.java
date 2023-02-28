package watcherbot.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import watcherbot.description.PageItemDescription;

@Component
public class OlxPageParser extends  PageParser{
    @Override
    protected Elements getElementCardsList(Document doc) {
        return doc.select("div[data-cy^=l-card]");
    }

    @Override
    protected PageItemDescription getItemFromCard(Element card) {
        Element itemElement = card.selectFirst("a");
        if (itemElement != null) {
            Element imgElement = itemElement.selectFirst("img");
            String photoUrl = imgElement.attr("src");
            String caption = imgElement.attr("alt");

            String itemUrl = itemElement.attr("href");
            itemUrl = "https://www.olx.pl" + itemUrl;
            String id = itemUrl.substring(itemUrl.length() - 12, itemUrl.length() - 5);

            return new PageItemDescription(id, itemUrl, photoUrl, caption);
        }
        return null;
    }

    @Override
    public String getDomainName() {
        return "olx";
    }
}
