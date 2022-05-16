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
public class AyPageTest {

    @Autowired
    AyPageParser ayPageParser;

    @Test
    public void getAllItems() {
        String url = "http://antiques.ay.by/retro-veschi/igrushki/yolochnye/?f\\=1&order\\=create&createdlots\\=1";
        assertTrue(ayPageParser.getAllItems(url).size() > 0);
    }
}