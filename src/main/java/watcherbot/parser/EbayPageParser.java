package watcherbot.parser;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import watcherbot.description.ItemDescription;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Component
public class EbayPageParser extends PageParser {
    @Override
    public Elements getElementCardsList(Document doc) {
//        Element count = doc.getElementsByClass("srp-controls__count-heading").first().selectFirst("span");
        Element count = doc.getElementsByClass("srp-controls__count-heading").first();
        count = (count.selectFirst("span") != null) ? count.selectFirst("span") : count;

        if (count.text().equals("0")) return new Elements() ;
        return doc.getElementsByClass("s-item__wrapper");
    }

    @Override
    public ItemDescription getItemFromCard(Element card) {
        Element imgElement = card.getElementsByClass("s-item__image-img").first();
        String photoUrl = imgElement.attr("src");
        String caption = imgElement.attr("alt");

        Element linkElement = card.getElementsByClass("s-item__link").first();
        String itemUrl = linkElement.attr("href");
        String id = itemUrl.substring(25, 37);

        return new ItemDescription(id, itemUrl, photoUrl, caption);
    }

    @Override
    public String getDomainName() {
        return "ebay";
    }
}
