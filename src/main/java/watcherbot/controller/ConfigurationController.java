package watcherbot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigurationController {
    @GetMapping("/health")
    @ResponseBody
    String healthcheck() {
        return "ok";
    }
}
