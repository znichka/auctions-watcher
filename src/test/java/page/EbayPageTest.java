package page;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EbayPageTest {

    @Test
    void getAllItems() {
        EbayPage page = new EbayPage("https://www.ebay.com/sch/i.html?_from=R40&_nkw=glass+garland&_sacat=907&_sop=10");
        assertTrue(page.getAllItems().size() > 0);
    }
}