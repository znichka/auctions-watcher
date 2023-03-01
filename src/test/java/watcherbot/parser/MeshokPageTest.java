package watcherbot.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import watcherbot.config.PageParserTestConfig;
import watcherbot.parser.page.MeshokPageParser;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("remotechrome")
@ContextConfiguration(classes = {PageParserTestConfig.class})
public class MeshokPageTest {
    @Autowired
    MeshokPageParser meshokPageParser;

    @Test
    public void getAllItems() {
        String url = "https://meshok.net/en?a=1&a_o=2&a_o=15&good=14299&pp=48&sort=beg_date&way=desc&reposted=N";
        assertTrue(meshokPageParser.getAllItems(url).size() > 0);

    }
    @Test
    public void getAllItemsSearch() {
        String url = "https://meshok.net/en/listing?a_o=15&good=14299&reposted=N&search=%D0%B1%D1%83%D1%81%D1%8B&sort=beg_date&way=desc&reposted=N";
        assertTrue(meshokPageParser.getAllItems(url).size() > 0);

    }

//    @AfterEach
//    public void cleanup(){
//        meshokPageParser.destroy();
//    }


}