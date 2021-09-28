package config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public enum HostType {
    MESHOK("meshok"),
    AYBY("ay.by"),
    KUFAR("kufar"),
    AVITO("avito"),
    EBAYCOM("ebay.com"),
    EBAYDE("ebay.de"),
    ETSY("etsy"),
    UNKNOWN("unknown");

    public final String host;

    HostType(String host) {
        this.host = host;
    }

    public static HostType getHostType(String url) throws MalformedURLException {
        String host = new URL(url).getHost().toLowerCase(Locale.ROOT);
        for (HostType e : HostType.values())
            if (host.contains(e.host)) return e;
        return UNKNOWN;
    }
}
