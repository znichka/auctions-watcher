package watcherbot.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EbayPageTest {

    @Test
    void getAllItems() {
        String url = "https://www.ebay.com/sch/i.html?_from=R40&_nkw=glass+garland&_sacat=907&_sop=10";
        EbayPageParser page = new EbayPageParser();
        assertTrue(page.getAllItems(url).size() > 0);
    }
}