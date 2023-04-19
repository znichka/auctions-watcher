package watcherbot.watchers;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import watcherbot.bot.TelegramBotSender;
import watcherbot.description.ItemDescription;
import watcherbot.description.ManagerDescription;
import watcherbot.service.ItemsService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MINUTES;

@Log
@Scope("prototype")
@Component
public class PageWatchersManager  {
    final ManagerDescription description;

    @Value("${noimage.link}")
    String NO_IMAGE_LINK;
    @Autowired
    TelegramBotSender sender;
    @Autowired
    ScheduledExecutorService scheduledExecutorService;
    @Autowired
    ItemsService itemsService;

    final List<PageWatcher> registeredPageWatchers;

    public PageWatchersManager(ManagerDescription description) {
        System.out.println("description adress in constructor" + description);
        this.description = description;

        registeredPageWatchers = new ArrayList<>();
    }

    public synchronized List<ItemDescription> filterUniqueItems(List<ItemDescription> items){
        return items.stream().filter(item ->  itemsService.insertIfUnique(item, description.getId()))
                      .peek(item -> {
                          if (item.getPhotoUrl() == null)
                              item.setPhotoUrl(NO_IMAGE_LINK);
                      })
                      .collect(Collectors.toList());
    }

    public void registerPageWatcher(PageWatcher pageWatcher){
        Runnable runnable = () -> {
            try {
                log.info(String.format("Run for a page - %s", pageWatcher.getDescription()));
                List<ItemDescription> newItems = filterUniqueItems(pageWatcher.getNewItems());
                if (newItems.size() != 0) {
                    log.info(String.format("New items for %s page for %s bot", pageWatcher.getDescription(), description.getName()));
                    send(newItems);
                }
            } catch (Exception e) {
                log.severe(String.format("Error while parsing the page. Url: %s", pageWatcher.getDescription()));
                log.severe(e.getMessage());
            }
        };
        synchronized (this) {
            scheduledExecutorService.scheduleAtFixedRate(runnable, 0, pageWatcher.getPeriod(), MINUTES);
            registeredPageWatchers.add(pageWatcher);
        }
        send("Page watcher for " + pageWatcher.getDescription() + " has been added");

    }

    public void send(List<ItemDescription> items) {
        log.info("Update for the " + description.getName() + " bot");
        for(ItemDescription item : items) {
            try  {
                sender.sendItemDescription(description.getCredentials(), item);
            } catch (IOException e) {
                log.severe(String.format("Error while sending item details to telegram bot %s. Item photo url: %s, item url: %s", description.getName(), item.getPhotoUrl(), item.getItemUrl()));
            }
        }
    }

    public void send(String message) {
        try {
            sender.sendMessage(description.getCredentials(), message);
        } catch (IOException e) {
            log.severe(String.format("Error while sending message to telegram bot %s. Message: %s", description.getName(), message));
        }
    }

    public int getId() {
        return description.getId();
    }
}
