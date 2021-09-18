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
    public final static Integer TIMEOUT_HOURS = 6;

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
            if (timeout >= TIMEOUT_HOURS && timeout % TIMEOUT_HOURS == 0)
                bot.sendMessage(String.format("There were no updates for %s for %d hours", pageDescription, timeout));
        } else {
            lastUpdate = LocalDateTime.now();
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
