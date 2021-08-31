import page.AuctionPage;

import java.io.IOException;

public class PageWatcher implements Runnable {
    AuctionPage page;
    TelegramBot bot;

    public PageWatcher(AuctionPage page, TelegramBot bot) throws IOException {
        this.page = page;
        this.bot = bot;
        bot.sendMessage("Watcher has started");
    }

    public void run() {
        page.getNewItems().forEach(item -> {
            try {
                bot.sendImageUpload(item.getPhotoUrl(), item.getCaption(), item.getItemUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
