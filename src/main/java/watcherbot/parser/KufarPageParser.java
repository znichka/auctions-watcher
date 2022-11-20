package watcherbot.parser;

import org.springframework.stereotype.Component;
import watcherbot.description.PageItemDescription;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Component
public class KufarPageParser extends PageParser {
    @Override
    public Elements getElementCardsList(Document doc) {
        Element article = doc.getElementsByTag("article").get(0);
        return article.getElementsByTag("a");
    }

    @Override
    public PageItemDescription getItemFromCard(Element card) {
        String itemUrl = card.attr("href");
        String id = itemUrl.substring(itemUrl.length() - 9);

        Element imgElement = card.getElementsByTag("img").first();

        String photoUrl = imgElement == null ? "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg" : imgElement.attr("data-src");
        String caption = imgElement == null ? "Елочная игрушка": imgElement.attr("alt");

        return new PageItemDescription(id, itemUrl, photoUrl, caption);
    }

    @Override
    public String getDomainName() {
        return "kufar";
    }
}
