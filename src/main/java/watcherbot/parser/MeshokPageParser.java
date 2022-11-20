package watcherbot.parser;

import org.springframework.stereotype.Component;
import watcherbot.description.PageItemDescription;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Component
public class MeshokPageParser extends PageParser {
    @Override
    public Elements getElementCardsList(Document doc) {
        return doc.select("div[class^=itemCardList]");
    }

    @Override
    public PageItemDescription getItemFromCard(Element card) {
        Element cardImage = card.select(".m-item-card-image").first();
        if (cardImage != null) {
            Element itemTitleElement = card.select("div[class^=itemTitle]").first();
            String id = itemTitleElement.attr("data-itemcard");

            String itemUrl = cardImage.attr("href");
            Element imgElement = cardImage.selectFirst("img");
            String photoUrl = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg";
            if (imgElement != null) photoUrl = imgElement.attr("src");
            String caption = itemTitleElement.text();
            itemUrl = "http://meshok.net" + itemUrl;

            return new PageItemDescription(id, itemUrl, photoUrl, caption);
        }
        return null;
    }

    @Override
    public String getDomainName() {
        return "meshok";
    }
}
