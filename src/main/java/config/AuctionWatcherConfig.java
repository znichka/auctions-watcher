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

    public static HashMap<HostType, TelegramBot> getBotsByConfig(ConfigDescription config){
        HashMap<HostType, TelegramBot> bots = new HashMap<>();
        for (Map.Entry<String, List<String>> token : config.getTokens().entrySet()){
            TelegramBot bot = new TelegramBot(token.getKey(), config.getUserId());
            for (String name : token.getValue()){
                HostType hostType = HostType.getHostType(name);
                bots.put(hostType, bot);
            }
        }
        return bots;
    }

    public static Collection<AuctionWatcher> generateAuctionWatchersByConfig(ConfigDescription config) {

        HashMap<HostType, TelegramBot> bots = getBotsByConfig(config);
        HashMap<HostType, AuctionWatcher> auctionWatchers = new HashMap<>();

        for (PageDescription pageDescription : config.getPages()) {
            try {
                HostType hostType = getHostType(pageDescription.getUrl());
                if (hostType == UNKNOWN)
                    throw new InvalidAttributesException(String.format("Unknown link type %s. Check the configuration file", pageDescription.getUrl()));

                if (!auctionWatchers.containsKey(hostType)) {
                    TelegramBot bot = bots.get(hostType);
                    PageParser parser = getParserByHostType(hostType);
                    if (bot == null) throw new InvalidAttributesException(String.format("There is no corresponding bot for that link: %s", pageDescription.getUrl()));
                    if (parser == null) throw new InvalidAttributesException(String.format("There is no corresponding parser for that link: %s", pageDescription.getUrl()));
                    auctionWatchers.put(hostType, new AuctionWatcher(bot, parser));
                }

                auctionWatchers.get(hostType).registerPage(pageDescription);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return auctionWatchers.values();
    }
}
