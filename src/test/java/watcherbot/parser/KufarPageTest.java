package watcherbot.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KufarPageTest {

    @Test
    void getAllItems() {
        String url = "https://www.kufar.by/l/antikvariat-i-kollekcii?query=%D0%B5%D0%BB%D0%BE%D1%87%D0%BD%D0%B0%D1%8F%20%D0%B8%D0%B3%D1%80%D1%83%D1%88%D0%BA%D0%B0";
        KufarPageParser page = new KufarPageParser();
        assertTrue(page.getAllItems(url).size() > 0);
    }
}