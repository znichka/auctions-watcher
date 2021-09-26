package page;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EtsyPageTest {

    @Test
    void getAllItems() {
        EtsyPage page = new EtsyPage("https://www.etsy.com/search/vintage?explicit=1&q=glass+vintage+ornament&ship_to=BY&order=date_desc");
        assertTrue(page.getAllItems().size() > 0);
    }
}