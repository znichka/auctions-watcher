package watcherbot.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import watcherbot.description.ManagerDescription;
import watcherbot.description.PageDescription;
import watcherbot.exception.ManagerNotFoundException;
import watcherbot.parser.PageParserFactory;
import watcherbot.repository.PageWatcherRepository;
import watcherbot.repository.PageWatchersManagerRepository;
import watcherbot.watchers.PageWatcher;
import watcherbot.watchers.PageWatchersManager;

import java.util.*;

@Service
@Transactional
public class PageWatcherService {
    Map<Integer, PageWatchersManager> workerList = new HashMap<>();;

    @Autowired
    PageWatchersManagerRepository pageWatchersManagerRepository;

    @Autowired
    PageParserFactory availableParsers;
    @Autowired
    ObjectProvider<PageWatcher> pageWatcherProvider;
    @Autowired
    ObjectProvider<PageWatchersManager> pageWatchersManagerProvider;

    public PageDescription addPage(PageDescription pageDescription, int managerId) {
        getManagerDescription(managerId).addPage(pageDescription);
        loadPageWatcher(pageDescription, managerId);
        return pageDescription;
    }

    public ManagerDescription addManager(ManagerDescription managerDescription) {
        pageWatchersManagerRepository.save(managerDescription);
        loadPageWatchersManager(managerDescription);
        return managerDescription;
    }


    public List<PageDescription> getAllPages(int managerId) {
        return getManagerDescription(managerId).getPages().stream().toList();
    }

    public List<ManagerDescription> getAllManagers() {
        return pageWatchersManagerRepository.findAll();
    }

    public ManagerDescription getManagerDescription(int managerId) {
        Optional<ManagerDescription> result = pageWatchersManagerRepository.findById(managerId);
        if (result.isEmpty()) throw new ManagerNotFoundException();
        return result.get();
    }

    @PostConstruct
    public void loadAllPageWatchersManagers() {
        for (ManagerDescription managerDescription : pageWatchersManagerRepository.findAll()) {
            loadPageWatchersManager(managerDescription);
        }
    }

    private void loadPageWatchersManager(ManagerDescription managerDescription) {
        PageWatchersManager manager = pageWatchersManagerProvider.getObject(managerDescription);
        workerList.put(manager.getId(), manager);
        managerDescription.getPages().forEach(p -> loadPageWatcher(p, managerDescription.getId()));
    }

    private void loadPageWatcher(PageDescription pageDescription, int managerId) {
        PageWatcher pageWatcher = pageWatcherProvider.getObject(availableParsers.getParserFor(pageDescription.getUrl()), pageDescription);
        getPageWatchersManager(managerId).registerPageWatcher(pageWatcher);
    }

    private PageWatchersManager getPageWatchersManager(int managerId) {
        if (!workerList.containsKey(managerId)) throw new ManagerNotFoundException();
        return workerList.get(managerId);
    }
}
