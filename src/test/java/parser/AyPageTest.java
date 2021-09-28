package parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AyPageTest {

    @Test
    void getAllItems() {
        String url = "http://antiques.ay.by/retro-veschi/igrushki/yolochnye/?f\\=1&order\\=create&createdlots\\=1";
        AyPageParser page = new AyPageParser();
        assertTrue(page.getAllItems(url).size() > 0);
    }
}