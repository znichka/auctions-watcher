package watcherbot.parser;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import watcherbot.config.PageParserTestConfig;

import javax.naming.OperationNotSupportedException;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class )
@ContextConfiguration(classes = {PageParserTestConfig.class})
public class ParserFactoryTest {
    @Autowired
    ParserFactory parserFactory;

    @Test
    public void getParserForValidUrl() {
        assertEquals(parserFactory.getParserFor("http://antiques.ay.by/").getClass(), AyPageParser.class);
        assertEquals(parserFactory.getParserFor("http://ay.by/").getClass(), AyPageParser.class);

        assertEquals(parserFactory.getParserFor("http://ebay.de/").getClass(), EbayPageParser.class);
        assertEquals(parserFactory.getParserFor("http://ebay.com").getClass(), EbayPageParser.class);
        assertEquals(parserFactory.getParserFor("http://avito.ru/").getClass(), AvitoPageParser.class);

        assertEquals(parserFactory.getParserFor("http://www.etsy.com/").getClass(), EtsyPageParser.class);
        assertEquals(parserFactory.getParserFor("http://etsy.com/").getClass(), EtsyPageParser.class);

        assertEquals(parserFactory.getParserFor("http://kufar.by/").getClass(), KufarPageParser.class);
        assertEquals(parserFactory.getParserFor("http://meshok.net/").getClass(), MeshokPageParser.class);
    }

    @Test
    public void getParserForInvalidUrl(){
        assertThrows(OperationNotSupportedException.class, () -> parserFactory.getParserFor("http://unknown.by/"));
        assertThrows(OperationNotSupportedException.class, () -> parserFactory.getParserFor("http://ayyy.by/"));
        assertThrows(OperationNotSupportedException.class, () -> parserFactory.getParserFor("http://aavitoo.by/"));
    }
}