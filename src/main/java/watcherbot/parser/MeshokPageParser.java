package watcherbot.parser;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import watcherbot.description.ItemDescription;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Component
public class MeshokPageParser extends PageParser {
    @Override
    public Elements getElementCardsList(Document doc) {
        return doc.getElementsByClass("itemCardList_2DhxS");
    }

    @Override
    public ItemDescription getItemFromCard(Element card) {
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

            return new ItemDescription(id, itemUrl, photoUrl, caption);
        }
        return null;
    }

    @Override
    public String getDomainName() {
        return "meshok";
    }
}
