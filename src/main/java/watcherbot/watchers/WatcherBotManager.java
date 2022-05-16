package watcherbot.watchers;

import lombok.Getter;
import lombok.extern.java.Log;
import watcherbot.bot.TelegramBotSender;
import watcherbot.description.BotCredentials;
import watcherbot.description.ItemDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Log
public class WatcherBotManager {

    private final TelegramBotSender sender;
    private final BotCredentials credentials;

    @Getter
    private final List<PageWatcher> registeredPageWatchers;
    private final HashSet<String> sentItems;

    public WatcherBotManager(TelegramBotSender sender, BotCredentials credentials) {
        this.sender = sender;
        this.credentials = credentials;

        sentItems = new HashSet<>();
        registeredPageWatchers = new ArrayList<>();
    }

    public void registerPageWatcher(PageWatcher watcher) {
        registeredPageWatchers.add(watcher);
    }

    public void send(List<ItemDescription> items) {
        for(ItemDescription item : items) {
            if (!sentItems.contains(item.getId())) {
                try {
                    sender.sendImageUpload(credentials, item.getPhotoUrl(), item.getCaption(), item.getItemUrl());
                } catch (IOException e) {
                    log.severe(String.format("Error while sending item details to telegram bot. Item photo url: %s, item url: %s", item.getPhotoUrl(), item.getItemUrl()));
                }
                sentItems.add(item.getId());
            }
        }
    }

    public void send(String message) {
        try {
            sender.sendMessage(credentials, message);
        } catch (IOException e) {
            log.severe(String.format("Error while sending message to telegram bot. Message: %s", message));
        }
    }
}
