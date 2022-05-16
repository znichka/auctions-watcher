package watcherbot.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import watcherbot.config.PageParserTestConfig;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class )
@ContextConfiguration(classes = {PageParserTestConfig.class})
public class EbayPageTest {
    @Autowired
    EbayPageParser ebayPageParser;

    @Test
    public void getAllItems() {
        String url = "https://www.ebay.com/sch/i.html?_from=R40&_nkw=glass+garland&_sacat=907&_sop=10";
        assertTrue(ebayPageParser.getAllItems(url).size() > 0);
    }
}