package watcherbot.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import watcherbot.description.ManagerDescription;
import watcherbot.description.PageDescription;
import watcherbot.exception.ManagerNotFoundException;
import watcherbot.parser.PageParserFactory;
import watcherbot.repository.PageWatcherRepository;
import watcherbot.repository.PageWatchersManagerRepository;
import watcherbot.watchers.PageWatcher;
import watcherbot.watchers.PageWatchersManager;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageWatcherService {
    Map<Integer, PageWatchersManager> managerList = new HashMap<>();;

    @Autowired
    PageWatcherRepository pageWatcherRepository;
    @Autowired
    PageWatchersManagerRepository pageWatchersManagerRepository;

    @Autowired
    PageParserFactory availableParsers;
    @Autowired
    ObjectProvider<PageWatcher> pageWatcherProvider;
    @Autowired
    ObjectProvider<PageWatchersManager> pageWatchersManagerProvider;

    public List<PageWatcher> getPages(int managerId) {
        return getManager(managerId).getRegisteredPageWatchers();
    }

    public PageWatcher add(PageDescription pageDescription, int managerId) {
        pageWatcherRepository.save(pageDescription);
        PageWatcher pageWatcher = pageWatcherProvider.getObject(availableParsers.getParserFor(pageDescription.getUrl()), pageDescription);
        getManager(managerId).registerPageWatcher(pageWatcher);
        return pageWatcher;
    }

    public PageWatchersManager add(ManagerDescription managerDescription) {
        pageWatchersManagerRepository.save(managerDescription);
        return loadPageWatchersManager(managerDescription);
    }

    public List<PageWatchersManager> getAllManagers() {
        return managerList.values().stream().toList();
    }

    public PageWatchersManager getManager(int managerId) {
        if (!managerList.containsKey(managerId)) throw new ManagerNotFoundException();
        return managerList.get(managerId);
    }

    @PostConstruct
    public void loadAllPageWatchersManagers() {
        for (ManagerDescription managerDescription : pageWatchersManagerRepository.findAll())
            loadPageWatchersManager(managerDescription);
    }

    public PageWatchersManager loadPageWatchersManager(ManagerDescription managerDescription) {
        PageWatchersManager manager = pageWatchersManagerProvider.getObject(managerDescription);
        managerList.put(manager.getDescription().getId(), manager);
        managerDescription.getPages().forEach(p -> add(p, manager.getDescription().getId()));
        return manager;
    }
}
