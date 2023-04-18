package watcherbot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({ItemsService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemsServiceTest {
    @Autowired
    ItemsService itemsService;

    @Test
    void ifAdded() {
        assertTrue(itemsService.register("1", "aaa", 1));
        assertFalse(itemsService.register("1", "aaa", 1));

        assertTrue(itemsService.register("3", "aaa", 2));
        assertFalse(itemsService.register("3", "aaa", 1));

        assertTrue(itemsService.register("2", "bbb", 1));
        assertTrue(itemsService.register("2", "bbb", 2));
    }
}