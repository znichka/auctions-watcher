package page;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AyPageTest {

    @Test
    void getAllItems() {
        AyPage page = new AyPage("http://antiques.ay.by/retro-veschi/igrushki/yolochnye/?f\\=1&order\\=create&createdlots\\=1");
        assertTrue(page.getAllItems().size() > 0);
    }
}