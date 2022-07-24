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

        url = "https://www.ebay.de/sch/i.html?_from=R40&_trksid=p2334524.m570.l1313&_nkw=wiltrud+elbert+christbaumschmuck&_sacat=0&LH_TitleDesc=0&_odkw=wiltrud+elbert&_osacat=0";
        assertTrue(ebayPageParser.getAllItems(url).size() == 0);


    }
}