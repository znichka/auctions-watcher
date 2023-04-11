package watcherbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import watcherbot.description.PageDescription;
import watcherbot.description.ManagerDescription;
import watcherbot.service.PageWatcherService;
import watcherbot.watchers.PageWatcher;
import watcherbot.watchers.PageWatchersManager;

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
        return service.getAllManagers().stream().map(PageWatchersManager::getDescription).toList();
    }

    @PostMapping("/bots")
    @ResponseBody
    ManagerDescription addManager(@RequestBody ManagerDescription managerDescription) {
        return service.add(managerDescription).getDescription();
    }

    @GetMapping("/bots/{id}/pages")
    @ResponseBody
    List<PageDescription> getAllPages(@PathVariable int id) {
        List<PageWatcher> list = service.getPages(id);
        if (list.size() == 0) return new ArrayList<>();
        return list.stream().map(PageWatcher::getDescription).toList();
    }

    @GetMapping("/bots/{id}")
    @ResponseBody
    ManagerDescription getManager(@PathVariable int id) {
        return service.getManager(id).getDescription();
    }

    @PostMapping("/bots/{id}/pages")
    @ResponseBody
    PageDescription addPage(@PathVariable("id") int id, @RequestBody PageDescription pageDescription) {
        return service.add(pageDescription, id).getDescription();
    }
}
