package watcherbot.parser;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import watcherbot.config.PageParserTestConfig;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PageParserTestConfig.class})
@Ignore
public class EtsyPageTest {

    @Autowired
    EtsyPageParser etsyPageParser;

    @Test
    public void getAllItems() {
        String url = "https://www.etsy.com/search/vintage?explicit=1&q=glass+vintage+ornament&ship_to=BY&order=date_desc";
        assertTrue(etsyPageParser.getAllItems(url).size() > 0);
    }
}