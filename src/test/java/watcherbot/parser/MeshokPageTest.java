package watcherbot.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import watcherbot.config.PageParserTestConfig;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PageParserTestConfig.class})
public class MeshokPageTest {
    @Autowired
    MeshokPageParser meshokPageParser;

    @Test
    public void getAllItems() {
        String url = "https://meshok.net/en?a=1&a_o=2&a_o=15&good=14299&pp=48&sort=beg_date&way=desc&reposted=N";
        assertTrue(meshokPageParser.getAllItems(url).size() > 0);
    }
}