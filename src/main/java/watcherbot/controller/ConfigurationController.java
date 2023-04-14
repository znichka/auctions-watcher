package watcherbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import watcherbot.description.PageDescription;
import watcherbot.description.ManagerDescription;
import watcherbot.service.PageWatcherService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ConfigurationController {
    @Autowired
    PageWatcherService service;

    @GetMapping("/health")
    @ResponseBody
    String healthcheck() {
        return "ok";
    }

    @GetMapping("/bots")
    @ResponseBody
    List<ManagerDescription> getAllManagers() {
        return service.getAllManagers();
    }

    @PostMapping("/bots")
    @ResponseBody
    ManagerDescription addManager(@RequestBody ManagerDescription managerDescription) {
        return service.addManager(managerDescription);//.getDescription();
    }

    @GetMapping("/bots/{id}/pages")
    @ResponseBody
    List<PageDescription> getAllPages(@PathVariable int id) {
        List<PageDescription> list = service.getAllPages(id);
        if (list.size() == 0) return new ArrayList<>();
        return list;
    }

    @GetMapping("/bots/{id}")
    @ResponseBody
    ManagerDescription getManager(@PathVariable int id) {
        return service.getManagerDescription(id);
    }

    @PostMapping("/bots/{id}/pages")
    @ResponseBody
    PageDescription addPage(@PathVariable("id") int id, @RequestBody PageDescription pageDescription) {
        return service.addPage(pageDescription, id);
    }
}
