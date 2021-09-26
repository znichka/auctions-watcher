import lombok.SneakyThrows;
import page.AuctionPage;
import page.ItemDescription;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PageWatcher implements Runnable {
    AuctionPage page;
    String pageDescription;
    TelegramBot bot;
    Integer period;
    LocalDateTime lastUpdate;
    final static Long TIMEOUT_HOURS = 6L;
    Long timeoutCheck = TIMEOUT_HOURS;

    public PageWatcher(AuctionPage page, String pageDescription, TelegramBot bot, Integer period) throws IOException {
        this.page = page;
        this.bot = bot;
        this.period = period;
        this.pageDescription = pageDescription;

        lastUpdate = LocalDateTime.now();
        bot.sendMessage(String.format("Watcher for %s has started", pageDescription));
    }

    @SneakyThrows
    public void run() {
        List<ItemDescription> newItems = page.getNewItems();
        if (newItems.size() == 0) {
            Long timeout = ChronoUnit.HOURS.between(lastUpdate, LocalDateTime.now());
            if (timeout >= timeoutCheck) {
                bot.sendMessage(String.format("There were no updates for %s for %d hours", pageDescription, timeoutCheck));
                timeoutCheck += TIMEOUT_HOURS;
            }
        } else {
            lastUpdate = LocalDateTime.now();
            timeoutCheck = TIMEOUT_HOURS;

            newItems.forEach(item -> {
                try {
                    bot.sendImageUpload(item.getPhotoUrl(), item.getCaption(), item.getItemUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
