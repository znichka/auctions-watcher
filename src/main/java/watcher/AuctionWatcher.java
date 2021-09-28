package watcher;

import bot.TelegramBot;
import description.ItemDescription;
import description.PageDescription;
import lombok.Getter;
import parser.PageParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MINUTES;

public class AuctionWatcher {
    TelegramBot bot;
    PageParser parser;
    @Getter
    List<PageWatcher> pageWatchers;
    HashSet<String> sentItems;

    public AuctionWatcher(TelegramBot bot, PageParser parser) {
        this.bot = bot;
        this.parser = parser;
        sentItems = new HashSet<>();
        pageWatchers = new ArrayList<>();
    }

    public void registerPage(PageDescription pageDescription) throws IOException {
        PageWatcher watcher = new PageWatcher(this, pageDescription);
        pageWatchers.add(watcher);
    }

    public void schedulePages(ScheduledExecutorService scheduler){
        pageWatchers.forEach(pageWatcher -> scheduler.scheduleAtFixedRate(pageWatcher, 0, pageWatcher.getPeriod(), MINUTES));
    }

    public void send(List<ItemDescription> items) {
        for(ItemDescription item : items) {
            if (!sentItems.contains(item.getId())) {
                try {
                    bot.sendImageUpload(item.getPhotoUrl(), item.getCaption(), item.getItemUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sentItems.add(item.getId());
            }
        }
    }

    public void send(String message) throws IOException {
        bot.sendMessage(message);
    }

    public List<ItemDescription> parse(String url) {
        return parser.getAllItems(url);
    }
}
