package watcherbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import watcherbot.config.ConfigurationControllerTestConfig;
import watcherbot.description.ManagerDescription;
import watcherbot.description.PageDescription;
import watcherbot.repository.PageWatcherRepository;
import watcherbot.repository.PageWatchersManagerRepository;
import watcherbot.service.PageWatcherService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = {ConfigurationControllerTestConfig.class})
@WebMvcTest(ConfigurationController.class)
//@DataJpaTest
//@SpringBootTest
//@AutoConfigureMockMvc
////@AutoConfigureDataJpa
//@AutoConfigureTestDatabase
class ConfigurationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void addPage() throws Exception {
        Path path = Path.of("src/test/resources/test-watcher-bot-description.json");
        File file = new File(path.toUri());
        assertTrue(file.exists());

        MvcResult response = mockMvc.perform(post("/bots").contentType(MediaType.APPLICATION_JSON).content(Files.readString(path)))
                .andExpect(status().isOk()).andReturn();

        ManagerDescription managerDescription = new ObjectMapper().readValue(response.getResponse().getContentAsString(), ManagerDescription.class);

        path = Path.of("src/test/resources/test-page-description.json");
        file = new File(path.toUri());
        assertTrue(file.exists());

        PageDescription pageDescription = new ObjectMapper().readValue(Files.readString(path), PageDescription.class);

        mockMvc.perform(post(String.format("/bots/%d/pages", managerDescription.getId())).contentType(MediaType.APPLICATION_JSON).content(Files.readString(path)))
                .andExpect(status().isOk());

        mockMvc.perform(get(String.format("/bots/%d/pages", managerDescription.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].url").value(pageDescription.getUrl()))
                .andExpect(jsonPath("$[0].description").value(pageDescription.getDescription()));
    }

    @Test
    void getAllManagers() throws Exception {
        Path path = Path.of("src/test/resources/test-watcher-bot-description.json");
        File file = new File(path.toUri());
        assertTrue(file.exists());

        ManagerDescription managerDescription = new ObjectMapper().readValue(Files.readString(path), ManagerDescription.class);

        mockMvc.perform(post("/bots").contentType(MediaType.APPLICATION_JSON).content(Files.readString(path)))
                        .andExpect(status().isOk());

        mockMvc.perform(get("/bots"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value(managerDescription.getName()));
//                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllPages() throws Exception {
        Path path = Path.of("src/test/resources/test-watcher-bot-description-with-pages.json");
        File file = new File(path.toUri());
        assertTrue(file.exists());

        MvcResult response = mockMvc.perform(post("/bots").contentType(MediaType.APPLICATION_JSON).content(Files.readString(path)))
                .andExpect(status().isOk()).andReturn();

        ManagerDescription managerDescription = new ObjectMapper().readValue(response.getResponse().getContentAsString(), ManagerDescription.class);

        mockMvc.perform(get(String.format("/bots/%d/pages", managerDescription.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].length()").value(managerDescription.getPages().size()));


        mockMvc.perform(get("/bots/100/pages"))
                .andExpect(status().isNotFound());

    }

    @Test
    void getNoPages() throws Exception {
        Path path = Path.of("src/test/resources/test-watcher-bot-description.json");
        File file = new File(path.toUri());
        assertTrue(file.exists());

        MvcResult response = mockMvc.perform(post("/bots").contentType(MediaType.APPLICATION_JSON).content(Files.readString(path)))
                .andExpect(status().isOk()).andReturn();

        ManagerDescription managerDescription = new ObjectMapper().readValue(response.getResponse().getContentAsString(), ManagerDescription.class);

        mockMvc.perform(get(String.format("/bots/%d/pages", managerDescription.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void healthcheck() throws Exception {
        mockMvc.perform(get("/health")).andExpect(status().isOk());
    }
}