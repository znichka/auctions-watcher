package watcherbot.watchers;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import watcherbot.description.ManagerDescription;
import watcherbot.bot.TelegramBotSender;
import watcherbot.description.ItemDescription;
import watcherbot.service.ItemsService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    TelegramBotSender sender;
    @Autowired
    ScheduledExecutorService scheduledExecutorService;
    @Autowired
    ItemsService itemsService;

    @Getter
    final List<PageWatcher> registeredPageWatchers;

    int healthCheckFixedRate;

    public PageWatchersManager(ManagerDescription description) {
        System.out.println("description adress in constructor" + description);
        this.description = description;

        registeredPageWatchers = new ArrayList<>();
    }

    public synchronized List<ItemDescription> deleteAlreadySentItems(List<ItemDescription> items){
        return items.stream().filter(item ->  itemsService.register(item.getId(), item.getPhotoHash(), description.getId()))
                      .collect(Collectors.toList());
    }

//    public synchronized List<ItemDescription> test(List<ItemDescription> items){
//        return items.stream().filter(item ->  !itemsService.insertIfUnique(item.getId(), item.getPhotoHash(), description.getId()))
//                .collect(Collectors.toList());
//    }

    public synchronized void registerPageWatcher(PageWatcher pageWatcher){
        Runnable runnable = () -> {
            try {
                log.info(String.format("Run for a page - %s", pageWatcher.getDescription()));
                List<ItemDescription> newItems = deleteAlreadySentItems(pageWatcher.getNewItems());
                if (newItems.size() != 0) {
                    log.info(String.format("New items for %s page for %s bot", pageWatcher.getDescription(), description.getName()));
                    send(newItems);
                }
            } catch (Exception e) {
                log.severe(String.format("Error while parsing the page. Url: %s", pageWatcher.getDescription()));
                log.severe(e.getMessage());
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(runnable, 0, pageWatcher.getPeriod(), MINUTES);

        registeredPageWatchers.add(pageWatcher);
        send("Page watcher for " + pageWatcher.getDescription() + " has been added");

    }

    public synchronized void send(List<ItemDescription> items) {
        log.info("Update for the " + description.getName() + " bot");
        for(ItemDescription item : items) {
            try  {
                sender.sendItemDescription(description.getCredentials(), item);
            } catch (IOException e) {
                log.severe(String.format("Error while sending item details to telegram bot %s. Item photo url: %s, item url: %s", description.getName(), item.getPhotoUrl(), item.getItemUrl()));
            }
        }
    }

    public synchronized void send(String message) {
        try {
            sender.sendMessage(description.getCredentials(), message);
        } catch (IOException e) {
            log.severe(String.format("Error while sending message to telegram bot %s. Message: %s", description.getName(), message));
        }
    }

    public int getId() {
        return description.getId();
    }

    @Scheduled(fixedRateString = "${healthcheck.fixedRate.in.milliseconds}") //todo move to another class
    public void run() {
        log.info(String.format("Performing healthcheck for %s bot", description.getName()));

//        int healthCheckRate = healthCheckFixedRate/60000;

        for (PageWatcher pageWatcher : registeredPageWatchers) {
            try {
                long timeout = ChronoUnit.MINUTES.between(pageWatcher.getTimestamp(), LocalDateTime.now());
                long notifyHours = pageWatcher.getNotify();

                if (timeout >= (notifyHours * 60)) {
                    timeout %= (notifyHours * 60);
                    if (timeout <= healthCheckFixedRate) {
                        String message = String.format("There were no updates for %s for last %d hours",
                                pageWatcher.getDescription(),
                                pageWatcher.getNotify());
                        send(message);
                        log.info(message);
                    }
                }
            } catch (Exception e) {
                log.warning("Error while running a health check for " + description.getName());
                log.warning(e.getMessage());
            }
        }
        log.info(String.format("Healthcheck finished for %s bot", description.getName()));
    }

    void setHealthCheckFixedRate(@Value("${healthcheck.fixedRate.in.milliseconds}") int fixedRate){
        this.healthCheckFixedRate = fixedRate/60000;
    }
}
