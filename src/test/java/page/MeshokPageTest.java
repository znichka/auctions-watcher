package page;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeshokPageTest {
    @Test
    void getAllItems() {
        MeshokPage page = new MeshokPage("https://meshok.net/en?a=1&a_o=2&a_o=15&good=14299&pp=48&sort=beg_date&way=desc&reposted=N");
        assertTrue(page.getAllItems().size() > 0);
    }

}