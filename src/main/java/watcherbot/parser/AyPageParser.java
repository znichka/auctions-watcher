package watcherbot.parser;

import org.springframework.stereotype.Component;
import watcherbot.description.PageItemDescription;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Component
public class AyPageParser extends PageParser {
    @Override
    public Elements getElementCardsList(Document doc) {
        return doc.select("li.viewer-type-grid__li[data-value] ");
    }

    @Override
    public PageItemDescription getItemFromCard(Element card) {
        String id = card.attr("data-value");

        Element item = card.selectFirst("a.item-type-card__link");
        String itemUrl = item.attr("href");
        String photoUrl = item.selectFirst("img").attr("src");
        String caption = item.getElementsByClass("item-type-card__title").first().text();
        return new PageItemDescription(id, itemUrl, photoUrl, caption);
    }

    @Override
    public String getDomainName() {
        return "ay";
    }
}
