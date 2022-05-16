package watcherbot.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EtsyPageTest {

    @Test
    void getAllItems() {
        String url = "https://www.etsy.com/search/vintage?explicit=1&q=glass+vintage+ornament&ship_to=BY&order=date_desc";
        EtsyPageParser page = new EtsyPageParser();
        assertTrue(page.getAllItems(url).size() > 0);
    }
}