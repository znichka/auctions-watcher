package config;

import bot.TelegramBot;
import description.ConfigDescription;
import description.PageDescription;
import parser.*;
import watcher.AuctionWatcher;

import javax.naming.directory.InvalidAttributesException;
import java.util.*;

import static config.HostType.*;

public class AuctionWatcherConfig {
    public static PageParser getParserByHostType(HostType hostType) {
        switch (hostType){
            case AYBY:
                return new AyPageParser();
            case MESHOK:
                return new MeshokPageParser();
            case AVITO:
                return new AvitoPageParser();
            case KUFAR:
                return new KufarPageParser();
            case ETSY:
                return new EtsyPageParser();
            case EBAYDE:
            case EBAYCOM:
                return new EbayPageParser();
            default:
                return null;
        }
    }

    public static Collection<AuctionWatcher> generateAuctionWatchersByConfig(ConfigDescription config) {
        TelegramBot bot = new TelegramBot(config.getToken(), config.getUserId());
        HashMap<HostType, AuctionWatcher> auctionWatchers = new HashMap<>();

        for (PageDescription pageDescription : config.getPages()) {
            try {
                HostType hostType = getHostType(pageDescription.getUrl());
                if (hostType == UNKNOWN)
                    throw new InvalidAttributesException(String.format("Unknown link type %s. Check the configuration file", pageDescription.getUrl()));

                if (!auctionWatchers.containsKey(hostType))
                    auctionWatchers.put(hostType, new AuctionWatcher(bot, getParserByHostType(hostType)));

                auctionWatchers.get(hostType).registerPage(pageDescription);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return auctionWatchers.values();
    }
}
